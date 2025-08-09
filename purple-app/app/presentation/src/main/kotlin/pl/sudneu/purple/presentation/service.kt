package pl.sudneu.purple.presentation

import com.zaxxer.hikari.HikariDataSource
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.http4k.client.OkHttp
import org.http4k.config.Environment
import org.http4k.core.then
import org.http4k.filter.ClientFilters
import org.http4k.server.Undertow
import org.http4k.server.asServer
import pl.sudneu.purple.domain.DocumentMetadataReceiver
import pl.sudneu.purple.domain.FetchDocument
import pl.sudneu.purple.domain.store.EmbedDocument
import pl.sudneu.purple.domain.store.StoreDocument
import pl.sudneu.purple.infrastructure.aws.withAws
import pl.sudneu.purple.infrastructure.openai.SplitDocument
import pl.sudneu.purple.infrastructure.openai.placeholder
import pl.sudneu.purple.infrastructure.openai.withOpenAi
import pl.sudneu.purple.infrastructure.postgresql.withPostgresql
import pl.sudneu.purple.logging.ApplicationEvents
import pl.sudneu.purple.logging.ApplicationStarted
import pl.sudneu.purple.presentation.PurpleEnvironment.API_PORT
import pl.sudneu.purple.presentation.PurpleEnvironment.KAFKA_TOPIC
import pl.sudneu.purple.presentation.PurpleEnvironment.OPEN_AI_URL_ENDPOINT

fun main(args: Array<String>) {
  val events = ApplicationEvents()
  val environment = Environment.ENV overrides localEnvironment

  val awsClient = OkHttp()

  val kafkaProperties = environment.toProperties()
  val datasource = HikariDataSource(environment.toHikariConfig())
  val awsParameters = environment.toAwsParameters(awsClient)

  val openAiClient = ClientFilters
    .SetBaseUriFrom(environment[OPEN_AI_URL_ENDPOINT])
    .then(OkHttp())

  val purpleApi = PurpleApi(datasource, openAiClient)

  val server = purpleApi.asServer(Undertow(environment[API_PORT]))
  Runtime.getRuntime().addShutdownHook(Thread { server.stop() })

  val metadataReceiver = DocumentMetadataReceiver(
    fetchDocument = FetchDocument.withAws(awsParameters),
    embedDocument = EmbedDocument.withOpenAi(openAiClient, SplitDocument.placeholder()),
    storeDocument = StoreDocument.withPostgresql(datasource)
  )
  val kafkaConsumer = KafkaConsumer(
    kafkaProperties,
    StringDeserializer(),
    FileReceivedEventDeserializer()
  )

  val messageHandler = PurpleMessageHandler(
    kafkaConsumer, metadataReceiver, events
  )

  server.start()
  messageHandler.listen(environment[KAFKA_TOPIC]).also { events(ApplicationStarted) }
}
