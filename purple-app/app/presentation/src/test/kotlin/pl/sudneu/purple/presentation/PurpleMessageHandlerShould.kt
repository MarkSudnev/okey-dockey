package pl.sudneu.purple.presentation

import dev.forkhandles.result4k.asSuccess
import io.kotest.matchers.collections.shouldContain
import io.mockk.every
import io.mockk.mockk
import org.apache.kafka.clients.consumer.MockConsumer
import org.apache.kafka.clients.consumer.OffsetResetStrategy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import pl.sudneu.purple.domain.Document
import pl.sudneu.purple.domain.DocumentMetadataReceiver
import pl.sudneu.purple.domain.EmbedDocument
import pl.sudneu.purple.domain.EmbeddedDocument
import pl.sudneu.purple.domain.FetchDocument
import pl.sudneu.purple.domain.NonBlankString
import pl.sudneu.purple.domain.StoreDocument
import java.util.Vector

class PurpleMessageHandlerShould {

  private val fetchDocument = mockk<FetchDocument>()
  private val embedDocument = mockk<EmbedDocument>()
  private val storeDocument = mockk<StoreDocument>()

  private val documentMetadataReceiver = DocumentMetadataReceiver(
    fetchDocument, embedDocument, storeDocument
  )

  @BeforeEach
  fun setup() {
    every { fetchDocument.invoke(any()) } returns Document(NonBlankString("Hello")).asSuccess()
    every { embedDocument.invoke(any()) } returns
      EmbeddedDocument(
        NonBlankString("Hello"),
      Vector(10, 0)
    ).asSuccess()
    every { storeDocument.invoke(any()) } returns Unit.asSuccess()
  }

  @Test
  fun `subscribe to topic`() {
    val topicName = "file-events"
    val consumer = MockConsumer<String, FileReceivedEvent>(OffsetResetStrategy.EARLIEST)
    val handler = PurpleMessageHandler(consumer, documentMetadataReceiver)

    handler.subscribe(topicName)

    consumer.subscription() shouldContain topicName
  }

  @Test
  fun `handle message`() {
    val topicName = "file-events"
    val consumer = MockConsumer<String, FileReceivedEvent>(OffsetResetStrategy.EARLIEST)
    val handler = PurpleMessageHandler(consumer, documentMetadataReceiver)

    handler.subscribe(topicName)
  }
}
