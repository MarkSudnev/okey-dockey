package pl.sudneu.purple.presentation

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.forkhandles.fabrikate.Fabrikate
import io.kotest.matchers.shouldBe
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.MockConsumer
import org.apache.kafka.clients.consumer.OffsetResetStrategy.EARLIEST
import org.apache.kafka.common.TopicPartition
import org.http4k.aws.AwsCredentials
import org.http4k.connect.amazon.core.model.Region
import org.http4k.connect.amazon.s3.FakeS3
import org.http4k.connect.amazon.s3.model.BucketKey
import org.http4k.connect.amazon.s3.model.BucketName
import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.testcontainers.containers.PostgreSQLContainer
import pl.sudneu.purple.domain.DocumentMetadataReceiver
import pl.sudneu.purple.domain.EmbedDocument
import pl.sudneu.purple.domain.FetchDocument
import pl.sudneu.purple.domain.StoreDocument
import pl.sudneu.purple.infrastructure.aws.withAws
import pl.sudneu.purple.infrastructure.openai.SplitDocument
import pl.sudneu.purple.infrastructure.openai.placeholder
import pl.sudneu.purple.infrastructure.openai.withOpenAi
import pl.sudneu.purple.infrastructure.postgresql.withPostgresql
import javax.sql.DataSource

class PurpleApplicationTest {

  // TODO: Put real aws s3 key to event
  // TODO: Put text document to s3 bucket

  private val bucketName = BucketName.of("bucket-name")
  private val region = Region.of("eu-central-1")
  private val documentKey = BucketKey.of("${bucketName.value}/document.txt")
  private val http: HttpHandler = FakeS3()
  private val credentialsProvider = { AwsCredentials("accesskey", "secret") }
  private val openaiClient: HttpHandler = { Response(OK).body(openaiResponse()) }

  private val datasource: DataSource by lazy {
    HikariConfig().also { config ->
      config.driverClassName = "org.postgresql.Driver"
      config.jdbcUrl = pgVectorContainer.jdbcUrl
      config.username = pgVectorContainer.username
      config.password = pgVectorContainer.password
      config.maximumPoolSize = 6
      config.isReadOnly = false
    }.let { HikariDataSource(it) }
  }

  private val metadataReceiver = DocumentMetadataReceiver(
    fetchDocument = FetchDocument.withAws(credentialsProvider, bucketName, region, http),
    embedDocument = EmbedDocument.withOpenAi(openaiClient, SplitDocument.placeholder()),
    storeDocument = StoreDocument.withPostgresql(datasource)
  )
  private val topicPartition = TopicPartition("metadata-topic", 0)
  private val consumer = MockConsumer<String, FileReceivedEvent>(EARLIEST)
  private val handler = PurpleMessageHandler(consumer, metadataReceiver)


  @BeforeEach
  fun setup() {
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

  }

  @Test
  @Disabled
  fun `should store vectorized document`() {
    val event: FileReceivedEvent = Fabrikate().random()
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
    datasource.connection.use { conn ->
      val result = conn
        .prepareStatement("SELECT * FROM documents")
        .executeQuery()
      result.next()
      result.row shouldBe 1
    }

  }

  companion object {
    val pgVectorContainer = PostgreSQLContainer("pgvector/pgvector:pg16")
      .withDatabaseName("dockey")
      .withUsername("root")
      .withPassword("root")

    @BeforeAll
    @JvmStatic
    fun runInfrastructure() {
      pgVectorContainer.start()
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
