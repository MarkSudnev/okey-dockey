package pl.sudneu.purple.presentation

import com.zaxxer.hikari.HikariDataSource
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.http4k.client.OkHttp
import org.http4k.config.Environment
import org.http4k.config.Secret
import org.http4k.connect.amazon.AWS_ACCESS_KEY_ID
import org.http4k.connect.amazon.AWS_REGION
import org.http4k.connect.amazon.AWS_SECRET_ACCESS_KEY
import org.http4k.connect.amazon.core.model.AccessKeyId
import org.http4k.connect.amazon.core.model.Region
import org.http4k.connect.amazon.core.model.SecretAccessKey
import org.http4k.connect.amazon.s3.model.BucketName
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.ClientFilters
import pl.sudneu.purple.domain.DocumentMetadataReceiver
import pl.sudneu.purple.domain.EmbedDocument
import pl.sudneu.purple.domain.FetchDocument
import pl.sudneu.purple.domain.StoreDocument
import pl.sudneu.purple.infrastructure.aws.withAws
import pl.sudneu.purple.infrastructure.openai.SplitDocument
import pl.sudneu.purple.infrastructure.openai.placeholder
import pl.sudneu.purple.infrastructure.openai.withOpenAi
import pl.sudneu.purple.infrastructure.postgresql.withPostgresql
import pl.sudneu.purple.logging.ApplicationEvents
import pl.sudneu.purple.logging.ApplicationStarted
import pl.sudneu.purple.presentation.PurpleEnvironment.AWS_BUCKET_NAME
import pl.sudneu.purple.presentation.PurpleEnvironment.AWS_URL_ENDPOINT
import pl.sudneu.purple.presentation.PurpleEnvironment.KAFKA_BOOTSTRAP_SERVERS
import pl.sudneu.purple.presentation.PurpleEnvironment.KAFKA_GROUP_ID
import pl.sudneu.purple.presentation.PurpleEnvironment.KAFKA_TOPIC
import pl.sudneu.purple.presentation.PurpleEnvironment.OPEN_AI_URL_ENDPOINT
import pl.sudneu.purple.presentation.PurpleEnvironment.VEC_DATABASE_PASSWORD
import pl.sudneu.purple.presentation.PurpleEnvironment.VEC_DATABASE_URL
import pl.sudneu.purple.presentation.PurpleEnvironment.VEC_DATABASE_USERNAME

fun main(args: Array<String>) {
  val events = ApplicationEvents()
  val environment = Environment.ENV overrides Environment.defaults(
    VEC_DATABASE_USERNAME of "admin",
    VEC_DATABASE_PASSWORD of Secret("admin"),
    VEC_DATABASE_URL of Uri.of("jdbc:postgresql://localhost:5432/dockey"),
    KAFKA_BOOTSTRAP_SERVERS of listOf("localhost:29092"),
    KAFKA_TOPIC of "dockey",
    KAFKA_GROUP_ID of "abc-123",
    AWS_URL_ENDPOINT of Uri.of("http://localhost:9000"),
    AWS_REGION of Region.of("us-east-1"),
    AWS_BUCKET_NAME of BucketName.of("dockey-bucket"),
    AWS_ACCESS_KEY_ID of AccessKeyId.of("user"),
    AWS_SECRET_ACCESS_KEY of SecretAccessKey.of("password"),
    OPEN_AI_URL_ENDPOINT of Uri.of("http://localhost:8090")
  )

  val awsClient = OkHttp()

  val kafkaProperties = environment.toProperties()
  val datasource = HikariDataSource(environment.toHikariConfig())
  val awsParameters = environment.toAwsParameters(awsClient)

  val kafkaConsumer = KafkaConsumer(
    kafkaProperties,
    StringDeserializer(),
    FileReceivedEventDeserializer()
  )

  val openAiClient = ClientFilters.SetBaseUriFrom(environment[OPEN_AI_URL_ENDPOINT]).then(OkHttp())

  val metadataReceiver = DocumentMetadataReceiver(
    fetchDocument = FetchDocument.withAws(awsParameters),
    embedDocument = EmbedDocument.withOpenAi(openAiClient, SplitDocument.placeholder()),
    storeDocument = StoreDocument.withPostgresql(datasource)
  )

  val messageHandler = PurpleMessageHandler(
    kafkaConsumer, metadataReceiver, events
  )

  messageHandler.listen(environment[KAFKA_TOPIC]).also { events(ApplicationStarted) }
}
