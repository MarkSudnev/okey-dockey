package pl.sudneu.purple.presentation

import dev.forkhandles.fabrikate.Fabrikate
import dev.forkhandles.result4k.asSuccess
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
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import pl.sudneu.purple.domain.DocumentMetadataReceiver
import pl.sudneu.purple.domain.EmbedDocument
import pl.sudneu.purple.domain.EmbeddedDocument
import pl.sudneu.purple.domain.FetchDocument
import pl.sudneu.purple.domain.StoreDocument
import pl.sudneu.purple.infrastructure.aws.withAws

class PurpleApplicationTest {

  private val bucketName = BucketName.of("bucket-name")
  private val region = Region.of("eu-central-1")
  private val documentKey = BucketKey.of("${bucketName.value}/document.txt")
  private val http: HttpHandler = FakeS3()
  private val credentialsProvider = { AwsCredentials("accesskey", "secret") }

  private val metadataReceiver = DocumentMetadataReceiver(
    fetchDocument = FetchDocument.withAws(credentialsProvider, bucketName, region, http),
    embedDocument = DummyEmbedDocument(),
    storeDocument = DummyStoreDocument()
  )
  private val topicPartition = TopicPartition("metadata-topic", 0)
  private val consumer = MockConsumer<String, FileReceivedEvent>(EARLIEST)
  private val handler = PurpleMessageHandler(consumer, metadataReceiver)

  @BeforeEach
  fun setup() {
    DatabaseUtils.prepare()
    consumer.updateBeginningOffsets(mutableMapOf(topicPartition to 0))
    consumer.schedulePollTask { consumer.rebalance(mutableListOf(topicPartition)) }
  }

  @AfterEach
  fun teardown() {
    DatabaseUtils.clean()
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
    val result = TestDatabaseConnection
      .dataSource
      .connection
      .prepareStatement("SELECT * FROM documents")
      .executeQuery()

    result.last()
    result.row shouldBe 1
  }
}

fun DummyEmbedDocument(): EmbedDocument =
  EmbedDocument { document ->
    listOf(EmbeddedDocument(document.content, emptyList())).asSuccess() }

fun DummyStoreDocument(): StoreDocument {
  return StoreDocument { Unit.asSuccess() }
}
