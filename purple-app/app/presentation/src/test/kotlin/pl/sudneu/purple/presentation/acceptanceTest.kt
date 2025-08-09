package pl.sudneu.purple.presentation

import com.zaxxer.hikari.HikariDataSource
import dev.forkhandles.fabrikate.Fabrikate
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldStartWith
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.MockConsumer
import org.apache.kafka.clients.consumer.OffsetResetStrategy.EARLIEST
import org.apache.kafka.common.TopicPartition
import org.http4k.aws.AwsCredentials
import org.http4k.client.OkHttp
import org.http4k.config.Environment
import org.http4k.config.Port
import org.http4k.config.Secret
import org.http4k.connect.amazon.AWS_ACCESS_KEY_ID
import org.http4k.connect.amazon.AWS_REGION
import org.http4k.connect.amazon.AWS_SECRET_ACCESS_KEY
import org.http4k.connect.amazon.s3.FakeS3
import org.http4k.connect.amazon.s3.Http
import org.http4k.connect.amazon.s3.S3
import org.http4k.connect.amazon.s3.S3Bucket
import org.http4k.connect.amazon.s3.createBucket
import org.http4k.connect.amazon.s3.model.BucketKey
import org.http4k.connect.amazon.s3.putObject
import org.http4k.contract.openapi.OpenAPIJackson.auto
import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.ClientFilters
import org.http4k.kotest.shouldHaveBody
import org.http4k.kotest.shouldHaveStatus
import org.http4k.server.SunHttp
import org.http4k.server.asServer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable
import org.testcontainers.containers.PostgreSQLContainer
import pl.sudneu.purple.domain.DocumentMetadataReceiver
import pl.sudneu.purple.domain.store.EmbedDocument
import pl.sudneu.purple.domain.FetchDocument
import pl.sudneu.purple.domain.store.StoreDocument
import pl.sudneu.purple.infrastructure.aws.withAws
import pl.sudneu.purple.infrastructure.openai.SplitDocument
import pl.sudneu.purple.infrastructure.openai.placeholder
import pl.sudneu.purple.infrastructure.openai.withOpenAi
import pl.sudneu.purple.infrastructure.postgresql.withPostgresql
import pl.sudneu.purple.presentation.PurpleEnvironment.AWS_BUCKET_NAME
import pl.sudneu.purple.presentation.PurpleEnvironment.KAFKA_TOPIC
import pl.sudneu.purple.presentation.PurpleEnvironment.VEC_DATABASE_DRIVER
import pl.sudneu.purple.presentation.PurpleEnvironment.VEC_DATABASE_PASSWORD
import pl.sudneu.purple.presentation.PurpleEnvironment.VEC_DATABASE_URL
import pl.sudneu.purple.presentation.PurpleEnvironment.VEC_DATABASE_USERNAME
import javax.sql.DataSource

@DisabledIfEnvironmentVariable(named = "SKIP_TEST_CONTAINERS", matches = "true")
class PurpleApplicationTest {

  private val bucketName = environment[AWS_BUCKET_NAME]
  private val region = environment[AWS_REGION]
  private val documentKey = BucketKey.of("document.txt")
  private val awsClient: HttpHandler = FakeS3()
  private val credentialsProvider = {
    AwsCredentials(
      environment[AWS_ACCESS_KEY_ID].toString(),
      environment[AWS_SECRET_ACCESS_KEY].toString()
    )
  }
  private val awsParameters = environment.toAwsParameters(awsClient)
  private val s3Bucket = S3Bucket.Http(bucketName, region, credentialsProvider, awsClient)
  private val openaiClient: HttpHandler = { Response(OK).body(openaiResponse()) }

  private val datasource: DataSource by lazy {
    HikariDataSource(environment.toHikariConfig())
  }

  private val metadataReceiver = DocumentMetadataReceiver(
    fetchDocument = FetchDocument.withAws(awsParameters),
    embedDocument = EmbedDocument.withOpenAi(openaiClient, SplitDocument.placeholder()),
    storeDocument = StoreDocument.withPostgresql(datasource)
  )
  private val topicPartition = TopicPartition(environment[KAFKA_TOPIC], 0)
  private val consumer = MockConsumer<String, FileReceivedEvent>(EARLIEST)
  private val handler = PurpleMessageHandler(consumer, metadataReceiver)

  private val service = PurpleApi(datasource, openaiClient).asServer(SunHttp(Port.RANDOM.value))
  private val client = ClientFilters
    .SetBaseUriFrom(Uri.of("http://localhost:${service.port()}"))
    .then(OkHttp())

  private fun createBucket() {
    val s3 = S3.Http(credentialsProvider, awsClient)
    s3.createBucket(bucketName, region)
  }

  @BeforeEach
  fun setup() {
    service.start()
    consumer.updateBeginningOffsets(mutableMapOf(topicPartition to 0))
    consumer.schedulePollTask { consumer.rebalance(mutableListOf(topicPartition)) }
    datasource.connection.use { connection ->
      connection.createStatement().execute("CREATE EXTENSION IF NOT EXISTS vector;")
      connection.createStatement().execute(
        """CREATE TABLE IF NOT EXISTS documents (
        id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
        content VARCHAR NOT NULL,
        embedding vector(1152) NOT NULL);"""
      )
    }
  }

  @AfterEach
  fun teardown() {
    service.stop()
  }

  @Test
  fun `should store vectorized document`() {
    createBucket()
    s3Bucket.putObject(documentKey, text.byteInputStream())
    val event: FileReceivedEvent = randomEvent(documentKey.value)
    consumer.schedulePollTask {
      consumer.addRecord(
        ConsumerRecord(
          topicPartition.topic(),
          topicPartition.partition(),
          0,
          "key",
          event
        )
      )
    }
    consumer.schedulePollTask { consumer.wakeup() }
    handler.listen(topicPartition.topic())

    val response = client(Request(GET, "/api/v1/documents?q=people%20suffered%20from%20fire"))

    response shouldHaveStatus OK
    searchDocumentsResponseBodyLens(response) shouldContain text
  }

  companion object {
    private val pgVectorContainer = PostgreSQLContainer("pgvector/pgvector:pg16")
      .withDatabaseName("dockey")
      .withUsername("root")
      .withPassword("root")
    private lateinit var _environment: Environment
    val environment: Environment get() = _environment

    @BeforeAll
    @JvmStatic
    fun runInfrastructure() {
      pgVectorContainer.start()

      _environment = Environment.defaults(
        VEC_DATABASE_DRIVER of "org.postgresql.Driver",
        VEC_DATABASE_URL of Uri.of(pgVectorContainer.jdbcUrl),
        VEC_DATABASE_USERNAME of pgVectorContainer.username,
        VEC_DATABASE_PASSWORD of Secret(pgVectorContainer.password),
      ) overrides testEnvironment
    }

    @AfterAll
    @JvmStatic
    fun stopInfrastructure() {
      pgVectorContainer.stop()
    }
  }
}

fun openaiResponse(): String =
  ClassLoader.getSystemResource("open-ai-embeddings-response.json").readText()

fun randomEvent(key: String): FileReceivedEvent {
  val randomMetadata: FileMetadata = Fabrikate().random()
  val metadata = randomMetadata.copy(key = key)
  val s3 = FileS3(metadata)
  val randomEvent = Fabrikate().random<FileReceivedEvent>()
  val event = randomEvent.copy(Records = listOf(FileRecord(s3)))
  return event
}

private val text = """Fire Department. One concertgoer was pronounced dead at the scene and 27 were
  taken to the hospital, of 48 who suffered non-fatal injuries. The street-facing facade and the
  upper roof structure were found on the street after the tornado. Following the collapse,
  the lack of safety protocols despite warning became the subject of multiple lawsuits.""".trimMargin()

private val searchDocumentsResponseBodyLens = Body.auto<List<String>>().toLens()
