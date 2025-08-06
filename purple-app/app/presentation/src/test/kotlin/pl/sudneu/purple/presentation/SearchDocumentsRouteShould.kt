package pl.sudneu.purple.presentation

import dev.forkhandles.result4k.asSuccess
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.I_M_A_TEAPOT
import org.http4k.core.Status.Companion.OK
import org.http4k.kotest.shouldHaveBody
import org.http4k.kotest.shouldHaveStatus
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.junit.jupiter.api.Test
import pl.sudneu.purple.domain.Document
import pl.sudneu.purple.domain.SearchDocuments

class SearchDocumentsRouteShould {

  @Test
  fun `return documents contents when they were found`() {
    val searchDocuments = SearchDocuments {
      listOf(Document("In diam sem aenean senectus.")).asSuccess()
    }
    val client = SearchDocumentsRoute(searchDocuments)
    with(client(Request(GET, "/api/v1/documents?q=lorem&n=3"))) {
      this shouldHaveStatus OK
      this shouldHaveBody "[In diam sem aenean senectus.]"
    }
  }
}

fun SearchDocumentsRoute(searchDocuments: SearchDocuments): RoutingHttpHandler {
  return "/api/v1/documents" bind GET to { request ->
    Response(I_M_A_TEAPOT)
  }
}
