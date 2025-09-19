package pl.sudneu.purple.presentation

import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.then
import org.http4k.filter.AllowAll
import org.http4k.filter.CorsPolicy
import org.http4k.filter.OriginPolicy
import org.http4k.filter.ServerFilters
import org.http4k.filter.ServerFilters.Cors
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

  val policy = CorsPolicy(
    originPolicy = OriginPolicy.AllowAll(),
    headers = listOf("origin", "Content-Type", "Content-Length", "Accept", "X-Requested-With"),
    methods = Method.entries,
    credentials = true,
    maxAge = 3600
  )
  val application = routes(SearchDocumentsRoute(searchDocuments))
  return ServerFilters
    .CatchAll()
    .then(Cors(policy))
    .then(application)
}
