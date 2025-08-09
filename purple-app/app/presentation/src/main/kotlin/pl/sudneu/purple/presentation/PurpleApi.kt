package pl.sudneu.purple.presentation

import org.http4k.core.HttpHandler
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.routes
import pl.sudneu.purple.domain.retrieve.DocumentFinder
import pl.sudneu.purple.domain.retrieve.EmbedDocumentQuery
import pl.sudneu.purple.domain.retrieve.RetrieveDocuments
import pl.sudneu.purple.infrastructure.openai.withOpenAi
import pl.sudneu.purple.infrastructure.postgresql.withPostgresql
import javax.sql.DataSource

fun PurpleApi(datasource: DataSource, client: HttpHandler): RoutingHttpHandler {
  val searchDocuments = DocumentFinder(
    embedDocumentQuery = EmbedDocumentQuery.withOpenAi(client),
    retrieveDocuments = RetrieveDocuments.withPostgresql(datasource)
  )
  return routes(SearchDocumentsRoute(searchDocuments))
}
