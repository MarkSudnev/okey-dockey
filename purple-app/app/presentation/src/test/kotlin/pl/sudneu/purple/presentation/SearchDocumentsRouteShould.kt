package pl.sudneu.purple.presentation

import dev.forkhandles.result4k.asFailure
import dev.forkhandles.result4k.asSuccess
import io.kotest.matchers.shouldBe
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.INTERNAL_SERVER_ERROR
import org.http4k.core.Status.Companion.OK
import org.http4k.core.then
import org.http4k.filter.ServerFilters
import org.http4k.kotest.shouldHaveBody
import org.http4k.kotest.shouldHaveStatus
import org.junit.jupiter.api.Test
import pl.sudneu.purple.domain.Document
import pl.sudneu.purple.domain.PurpleError
import pl.sudneu.purple.domain.retrieve.SearchDocuments

class SearchDocumentsRouteShould {

  @Test
  fun `return documents contents when they were found`() {
    val documents = listOf(
      Document("alpha.txt", "In diam sem aenean senectus.",),
      Document("beta.txt", "Convallis convallis curae habitasse.",),
      Document("gamma.txt", "Facilisis auctor nascetur ex habitasse ipsum posuere.",)
    )
    val searchDocuments = SearchDocuments { documents.asSuccess() }
    val expectedResponse = documents.map(Document::toDocumentResponse)
    val client = SearchDocumentsRoute(searchDocuments)
    with(client(Request(GET, "/api/v1/documents?q=lorem&n=3"))) {
      this shouldHaveStatus OK
      documentsResponseBodyLens(this) shouldBe expectedResponse
    }
  }

  @Test
  fun `return empty list when nothing is found`() {
    val searchDocuments = SearchDocuments { emptyList<Document>().asSuccess() }
    val client = SearchDocumentsRoute(searchDocuments)
    with(client(Request(GET, "/api/v1/documents?q=lorem&n=3"))) {
      this shouldHaveStatus OK
      this shouldHaveBody """[]"""
    }
  }

  @Test
  fun `return bad request when query is missed`() {
    val searchDocuments = SearchDocuments { emptyList<Document>().asSuccess() }
    val client = ServerFilters
      .CatchLensFailure
      .then(SearchDocumentsRoute(searchDocuments))
    client(Request(GET, "/api/v1/documents?n=3")) shouldHaveStatus BAD_REQUEST
  }

  @Test
  fun `return bad request when results number is negative`() {
    val searchDocuments = SearchDocuments { emptyList<Document>().asSuccess() }
    val client = ServerFilters
      .CatchLensFailure
      .then(SearchDocumentsRoute(searchDocuments))
    client(Request(GET, "/api/v1/documents?q=Lorem%20Ipsum&n=-3")) shouldHaveStatus BAD_REQUEST
  }

  @Test
  fun `return internal server error when search documents is failed`() {
    val searchDocuments = SearchDocuments {
      PurpleError.SearchDocumentError("some-error").asFailure()
    }
    val client = ServerFilters
      .CatchLensFailure
      .then(SearchDocumentsRoute(searchDocuments))
    client(Request(GET, "/api/v1/documents?q=lorem&n=3")) shouldHaveStatus INTERNAL_SERVER_ERROR
  }

  @Test
  fun `return internal server error when unexpected error is happened`() {
    val searchDocuments = SearchDocuments { error("unexpected-error") }
    val client = ServerFilters
      .CatchLensFailure
      .then(SearchDocumentsRoute(searchDocuments))
    client(Request(GET, "/api/v1/documents?q=lorem&n=3")) shouldHaveStatus INTERNAL_SERVER_ERROR
  }
}
