package pl.sudneu.purple.presentation

import dev.forkhandles.fabrikate.Fabrikate
import dev.forkhandles.result4k.Success
import dev.forkhandles.result4k.asFailure
import dev.forkhandles.result4k.asSuccess
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.maps.shouldHaveKey
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.MockConsumer
import org.apache.kafka.clients.consumer.OffsetAndMetadata
import org.apache.kafka.clients.consumer.OffsetResetStrategy.EARLIEST
import org.apache.kafka.common.TopicPartition
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import pl.sudneu.purple.domain.Document
import pl.sudneu.purple.domain.DocumentMetadataReceiver
import pl.sudneu.purple.domain.EmbedDocument
import pl.sudneu.purple.domain.EmbeddedDocument
import pl.sudneu.purple.domain.FetchDocument
import pl.sudneu.purple.domain.PurpleError
import pl.sudneu.purple.domain.StoreDocument

class PurpleMessageHandlerShould {

  private val mockedFetchDocument = mockk<FetchDocument>()
  private val mockedEmbedDocument = mockk<EmbedDocument>()
  private val mockedStoreDocument = mockk<StoreDocument>()
  private val topicName = "file-events"
  private val topicPartition = TopicPartition(topicName, 0)
  private val consumer = MockConsumer<String, FileReceivedEvent>(EARLIEST)
  private val randomEvent: FileReceivedEvent = Fabrikate().random()

  @BeforeEach
  fun setup() {
    every { mockedFetchDocument.invoke(any()) } returns Document("Hello").asSuccess()
    every { mockedEmbedDocument.invoke(any()) } returns
      EmbeddedDocument("Hello", emptyList()).asSuccess()
    every { mockedStoreDocument.invoke(any()) } returns Unit.asSuccess()
  }

  @Test
  fun `subscribe to topic`() {
    val handler = PurpleMessageHandler(consumer, documentMetadataReceiver())
    prepareConsumer()
    consumer.schedulePollTask { sendMessage() }
    stopConsumer()
    handler.listen(topicName)
    consumer.subscription() shouldContain topicName
  }

  @Test
  fun `call document metadata handler`() {
    val storage = mutableListOf<EmbeddedDocument>()
    val documentMetadataReceiver =
      documentMetadataReceiver(storeDocument = DummyDocumentStorer(storage))
    val handler = PurpleMessageHandler(consumer, documentMetadataReceiver)
    prepareConsumer()
    consumer.schedulePollTask { sendMessage() }
    stopConsumer()
    handler.listen(topicPartition.topic())

    storage.shouldNotBeEmpty()
  }

  @Test
  fun `commit handled message`() {
    val handler = PurpleMessageHandler(consumer, documentMetadataReceiver())
    prepareConsumer()
    consumer.schedulePollTask { sendMessage() }
    lateinit var committed: MutableMap<TopicPartition, OffsetAndMetadata>
    consumer.schedulePollTask { committed = consumer.committed(setOf(topicPartition)) }
    stopConsumer()
    handler.listen(topicPartition.topic())
    committed shouldHaveSize 1
    committed shouldHaveKey topicPartition
  }

  @Test
  fun `not commit when document metadata handler is failed`() {
    val handler = PurpleMessageHandler(consumer) { PurpleError.UnknownError.asFailure() }
    prepareConsumer()
    consumer.schedulePollTask { sendMessage() }
    lateinit var committed: MutableMap<TopicPartition, OffsetAndMetadata>
    consumer.schedulePollTask { committed = consumer.committed(setOf(topicPartition)) }
    stopConsumer()
    handler.listen(topicPartition.topic())
    committed.shouldBeEmpty()
  }

  @Test
  fun `close consumer after shutdown`() {
    val handler = PurpleMessageHandler(consumer, documentMetadataReceiver())
    prepareConsumer()
    consumer.schedulePollTask { sendMessage() }
    stopConsumer()
    handler.listen(topicPartition.topic())
    consumer.closed() shouldBe true
  }

  private fun documentMetadataReceiver(
    fetchDocument: FetchDocument = mockedFetchDocument,
    embedDocument: EmbedDocument = mockedEmbedDocument,
    storeDocument: StoreDocument = mockedStoreDocument
  ) = DocumentMetadataReceiver(
    fetchDocument, embedDocument, storeDocument
  )

  private fun sendMessage(event: FileReceivedEvent = randomEvent) {
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

  private fun prepareConsumer() {
    val offset = 0L
    consumer.updateBeginningOffsets(mapOf(topicPartition to offset))
    consumer.schedulePollTask { consumer.rebalance(mutableListOf(topicPartition)) }
  }

  private fun stopConsumer() {
    consumer.schedulePollTask { consumer.wakeup() }
  }
}

internal fun DummyDocumentStorer(storage: MutableList<EmbeddedDocument>): StoreDocument =
  StoreDocument { doc -> Success(Unit).also { storage.add(doc) } }

