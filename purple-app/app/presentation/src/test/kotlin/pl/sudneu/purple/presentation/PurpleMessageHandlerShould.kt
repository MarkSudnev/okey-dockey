package pl.sudneu.purple.presentation

import dev.forkhandles.fabrikate.Fabrikate
import dev.forkhandles.result4k.Success
import dev.forkhandles.result4k.asSuccess
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.mockk.every
import io.mockk.mockk
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.MockConsumer
import org.apache.kafka.clients.consumer.OffsetResetStrategy
import org.apache.kafka.common.TopicPartition
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import pl.sudneu.purple.domain.Document
import pl.sudneu.purple.domain.DocumentMetadataReceiver
import pl.sudneu.purple.domain.EmbedDocument
import pl.sudneu.purple.domain.EmbeddedDocument
import pl.sudneu.purple.domain.FetchDocument
import pl.sudneu.purple.domain.NonBlankString
import pl.sudneu.purple.domain.StoreDocument
import java.util.*

class PurpleMessageHandlerShould {

  private val mockedFetchDocument = mockk<FetchDocument>()
  private val mockedEmbedDocument = mockk<EmbedDocument>()
  private val mockedStoreDocument = mockk<StoreDocument>()
  private val topicName = "file-events"
  private val topicPartition = TopicPartition(topicName, 0)
  private val consumer =
    MockConsumer<String, FileReceivedEvent>(OffsetResetStrategy.EARLIEST)

  @BeforeEach
  fun setup() {
    every { mockedFetchDocument.invoke(any()) } returns Document(NonBlankString("Hello")).asSuccess()
    every { mockedEmbedDocument.invoke(any()) } returns
      EmbeddedDocument(
        NonBlankString("Hello"),
        Vector(10, 0)
      ).asSuccess()
    every { mockedStoreDocument.invoke(any()) } returns Unit.asSuccess()
  }

  @Test
  fun `subscribe to topic`() {
    val consumer = MockConsumer<String, FileReceivedEvent>(OffsetResetStrategy.EARLIEST)
    val handler = PurpleMessageHandler(consumer, documentMetadataReceiver())

    handler.listen(topicName)

    consumer.subscription() shouldContain topicName
  }

  @Test
  fun `call document metadata handler`() {
    val storage = mutableListOf<EmbeddedDocument>()
    val event: FileReceivedEvent = Fabrikate().random()
    prepareConsumer()
    val documentMetadataReceiver =
      documentMetadataReceiver(storeDocument = DummyDocumentStorer(storage))
    val handler = PurpleMessageHandler(consumer, documentMetadataReceiver)
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

    storage.shouldNotBeEmpty()
  }

  @Test
  fun `handle message`() {
    val consumer = MockConsumer<String, FileReceivedEvent>(OffsetResetStrategy.EARLIEST)
    val documentMetadataReceiver = documentMetadataReceiver()
    val handler = PurpleMessageHandler(consumer, documentMetadataReceiver)

    handler.listen(topicName)
  }

  private fun documentMetadataReceiver(
    fetchDocument: FetchDocument = mockedFetchDocument,
    embedDocument: EmbedDocument = mockedEmbedDocument,
    storeDocument: StoreDocument = mockedStoreDocument
  ) = DocumentMetadataReceiver(
    fetchDocument, embedDocument, storeDocument
  )

  private fun prepareConsumer() {
    val offset = 0L
    consumer.updateBeginningOffsets(mapOf(topicPartition to offset))
    consumer.schedulePollTask { consumer.rebalance(mutableListOf(topicPartition)) }
  }

  private fun stop() {
    consumer.schedulePollTask { consumer.wakeup() }
  }
}

fun DummyDocumentStorer(storage: MutableList<EmbeddedDocument>): StoreDocument =
  StoreDocument { doc -> Success(Unit).also { storage.add(doc) } }

