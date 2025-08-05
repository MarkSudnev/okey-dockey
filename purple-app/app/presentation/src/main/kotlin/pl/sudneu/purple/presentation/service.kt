package pl.sudneu.purple.presentation

import com.zaxxer.hikari.HikariDataSource
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.http4k.client.OkHttp
import org.http4k.config.Environment
import org.http4k.connect.amazon.AWS_REGION
import org.http4k.connect.amazon.CredentialsProvider
import org.http4k.connect.amazon.Environment
import org.http4k.connect.amazon.s3.model.BucketName
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.then
import org.http4k.filter.ClientFilters
import pl.sudneu.purple.domain.DocumentMetadataReceiver
import pl.sudneu.purple.domain.EmbedDocument
import pl.sudneu.purple.domain.FetchDocument
import pl.sudneu.purple.domain.StoreDocument
import pl.sudneu.purple.infrastructure.aws.AwsFetchDocument
import pl.sudneu.purple.infrastructure.aws.withAws
import pl.sudneu.purple.infrastructure.openai.OpenAiEmbedDocument
import pl.sudneu.purple.infrastructure.openai.SplitDocument
import pl.sudneu.purple.infrastructure.openai.placeholder
import pl.sudneu.purple.infrastructure.openai.withOpenAi
import pl.sudneu.purple.infrastructure.postgresql.withPostgresql
import pl.sudneu.purple.logging.ApplicationEvents
import pl.sudneu.purple.logging.ApplicationStarted
import pl.sudneu.purple.presentation.PurpleEnvironment.AWS_BUCKET_NAME
import pl.sudneu.purple.presentation.PurpleEnvironment.AWS_URL_ENDPOINT
import pl.sudneu.purple.presentation.PurpleEnvironment.KAFKA_TOPIC
import pl.sudneu.purple.presentation.PurpleEnvironment.OPEN_AI_URL_ENDPOINT

fun main(args: Array<String>) {
  val events = ApplicationEvents()
  val environment = Environment.ENV

  val kafkaProperties = environment.toProperties()
  val datasource = HikariDataSource(environment.toHikariConfig())

  val kafkaConsumer = KafkaConsumer(
    kafkaProperties,
    StringDeserializer(),
    FileReceivedEventDeserializer()
  )

  val awsClient = ClientFilters.SetBaseUriFrom(environment[AWS_URL_ENDPOINT]).then(OkHttp())
  val openAiClient = ClientFilters.SetBaseUriFrom(environment[OPEN_AI_URL_ENDPOINT]).then(OkHttp())

  val metadataReceiver = DocumentMetadataReceiver(
    fetchDocument = FetchDocument.withAws(
      CredentialsProvider.Environment(environment),
      BucketName.of(environment[AWS_BUCKET_NAME]),
      environment[AWS_REGION],
      awsClient
    ),
    embedDocument = EmbedDocument.withOpenAi(openAiClient, SplitDocument.placeholder()),
    storeDocument = StoreDocument.withPostgresql(datasource)
  )

  val messageHandler = PurpleMessageHandler(
    kafkaConsumer, metadataReceiver, events
  )

  messageHandler.listen(environment[KAFKA_TOPIC]).also { events(ApplicationStarted) }
}
