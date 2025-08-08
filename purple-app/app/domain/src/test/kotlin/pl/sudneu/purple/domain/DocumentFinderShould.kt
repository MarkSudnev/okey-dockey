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
import pl.sudneu.purple.domain.PurpleError.EmbedDocumentQueryError
import pl.sudneu.purple.domain.PurpleError.RetrieveDocumentsError
import pl.sudneu.purple.domain.PurpleError.UnexpectedError
import pl.sudneu.purple.shared.toPositiveInteger

class DocumentFinderShould {

  private val embedDocumentQuery = mockk<EmbedDocumentQuery>()
  private val retrieveDocuments = mockk<RetrieveDocuments>()

  @BeforeEach
  fun setup() {
    every { embedDocumentQuery(any()) } returns EmbeddedQuery(
      emptyList(),
      3.toPositiveInteger()
    ).asSuccess()
    every { retrieveDocuments(any()) } returns listOf(document()).asSuccess()
  }

  @Test
  fun `call collaborators`() {
    val searchDocuments = DocumentFinder(embedDocumentQuery, retrieveDocuments)

    searchDocuments(DocumentQuery("Hello", 3)).shouldBeSuccess()
    verify { embedDocumentQuery(any()) }
    verify { retrieveDocuments(any()) }

  }

  @Test
  fun `return failure when embed document query is failed`() {
    val embedDocumentQuery = EmbedDocumentQuery {
      EmbedDocumentQueryError("some-error").asFailure()
    }
    val searchDocuments = DocumentFinder(embedDocumentQuery, retrieveDocuments)

    searchDocuments(DocumentQuery("Hello", 3)) shouldBeFailure
      EmbedDocumentQueryError("some-error")
  }

  @Test
  fun `return failure when retrieve documents is failed`() {
    val retrieveDocuments = RetrieveDocuments {
      RetrieveDocumentsError("some-error").asFailure()
    }
    val searchDocuments = DocumentFinder(embedDocumentQuery, retrieveDocuments)

    searchDocuments(DocumentQuery("Hello", 3)) shouldBeFailure
      RetrieveDocumentsError("some-error")
  }

  @Test
  fun `return failure when collaborators throw exception`() {
    val embedDocumentQuery = EmbedDocumentQuery { error("error-message") }

    val searchDocuments = DocumentFinder(embedDocumentQuery, retrieveDocuments)

    searchDocuments(DocumentQuery("Hello", 3)) shouldBeFailure
      UnexpectedError("IllegalStateException: error-message")
  }
}

private fun document(): Document = Fabrikate().random()
