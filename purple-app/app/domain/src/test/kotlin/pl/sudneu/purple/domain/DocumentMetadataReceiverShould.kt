package pl.sudneu.purple.domain

import dev.forkhandles.fabrikate.Fabrikate
import dev.forkhandles.result4k.asFailure
import dev.forkhandles.result4k.asSuccess
import dev.forkhandles.result4k.kotest.shouldBeFailure
import dev.forkhandles.result4k.kotest.shouldBeSuccess
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import pl.sudneu.purple.domain.PurpleError.EmbedDocumentError
import pl.sudneu.purple.domain.PurpleError.FetchDocumentError
import pl.sudneu.purple.domain.PurpleError.StoreDocumentError
import pl.sudneu.purple.domain.PurpleError.UnexpectedError

class DocumentMetadataReceiverTest {

  private val fetchDocument = mockk<FetchDocument>()
  private val embedDocument = mockk<EmbedDocument>()
  private val storeDocument = mockk<StoreDocument>()

  @BeforeEach
  fun setup() {
    every { fetchDocument.invoke(any()) } returns document().asSuccess()
    every { embedDocument.invoke(any()) } returns embeddedDocument().asSuccess()
    every { storeDocument.invoke(any()) } returns Unit.asSuccess()
  }

  @Test
  fun `call collaborators`() {
    val metadata: DocumentMetadata = Fabrikate().random()
    val documentMetadataHandler = DocumentMetadataReceiver(
      fetchDocument, embedDocument, storeDocument
    )

    documentMetadataHandler(metadata).shouldBeSuccess()
    verify { fetchDocument.invoke(any()) }
    verify { embedDocument.invoke(any()) }
    verify { storeDocument.invoke(any()) }
  }

  @Test
  fun `return failure when fetch document is failed`() {
    every { fetchDocument.invoke(any()) } returns FetchDocumentError("error-message").asFailure()

    val metadata: DocumentMetadata = Fabrikate().random()
    val documentMetadataHandler = DocumentMetadataReceiver(
      fetchDocument, embedDocument, storeDocument
    )

    documentMetadataHandler(metadata).shouldBeFailure(FetchDocumentError("error-message"))
  }

  @Test
  fun `return failure when embed document is failed`() {
    every { embedDocument.invoke(any()) } returns EmbedDocumentError("error-message").asFailure()

    val metadata: DocumentMetadata = Fabrikate().random()
    val documentMetadataHandler = DocumentMetadataReceiver(
      fetchDocument, embedDocument, storeDocument
    )

    documentMetadataHandler(metadata).shouldBeFailure(EmbedDocumentError("error-message"))
  }

  @Test
  fun `return failure when store document is failed`() {
    every { storeDocument.invoke(any()) } returns StoreDocumentError("error-message").asFailure()

    val metadata: DocumentMetadata = Fabrikate().random()
    val documentMetadataHandler = DocumentMetadataReceiver(
      fetchDocument, embedDocument, storeDocument
    )

    documentMetadataHandler(metadata).shouldBeFailure(StoreDocumentError("error-message"))
  }

  @Test
  fun `return failure when collaborators throw exception`() {
    val fetchDocument = FetchDocument { error("error-message") }

    val metadata: DocumentMetadata = Fabrikate().random()
    val documentMetadataHandler = DocumentMetadataReceiver(
      fetchDocument, embedDocument, storeDocument
    )

    documentMetadataHandler(metadata).shouldBeFailure(UnexpectedError("IllegalStateException: error-message"))
  }
}


internal fun document(): Document = Fabrikate().random()

internal fun embeddedDocument(): EmbeddedDocument =
  Fabrikate().random()
