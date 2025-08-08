package pl.sudneu.purple.presentation

import dev.forkhandles.result4k.map
import dev.forkhandles.result4k.recover
import org.http4k.core.Body
import org.http4k.core.Method.GET
import org.http4k.core.Response
import org.http4k.core.Status.Companion.INTERNAL_SERVER_ERROR
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with
import org.http4k.format.Jackson.auto
import org.http4k.lens.Query
import org.http4k.lens.int
import org.http4k.lens.nonBlankString
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import pl.sudneu.purple.domain.DocumentQuery
import pl.sudneu.purple.domain.SearchDocuments
import pl.sudneu.purple.domain.handleException
import pl.sudneu.purple.shared.NonBlankString
import pl.sudneu.purple.shared.PositiveInteger
import pl.sudneu.purple.shared.toPositiveInteger

val documentsResponseBodyLens = Body.auto<List<String>>().toLens()
val documentQueryRequestLens = Query
  .nonBlankString()
  .map(::NonBlankString, NonBlankString::toString)
  .required("q")
val documentCountRequestLens = Query.int()
  .map(::PositiveInteger, PositiveInteger::value)
  .defaulted("n", 3.toPositiveInteger())

fun SearchDocumentsRoute(searchDocuments: SearchDocuments): RoutingHttpHandler =
  "/api/v1/documents" bind GET to { request ->
    val query = documentQueryRequestLens(request)
    val count = documentCountRequestLens(request)
    val documentQuery = DocumentQuery(query, count)
    handleException {
      searchDocuments(documentQuery)
        .map { documents -> documents.map { doc -> doc.content.value } }
        .map { results -> Response(OK).with(documentsResponseBodyLens of results) }
    }
      .recover { Response(INTERNAL_SERVER_ERROR).body(it.message) }
  }
