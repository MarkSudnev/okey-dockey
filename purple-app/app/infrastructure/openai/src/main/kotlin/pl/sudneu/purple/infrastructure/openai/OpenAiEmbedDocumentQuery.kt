package pl.sudneu.purple.infrastructure.openai

import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.map
import dev.forkhandles.result4k.mapFailure
import org.http4k.core.HttpHandler
import pl.sudneu.purple.domain.PurpleError.EmbedDocumentQueryError
import pl.sudneu.purple.domain.retrieve.EmbedDocumentQuery
import pl.sudneu.purple.domain.retrieve.EmbeddedQuery

fun OpenAiEmbedDocumentQuery(client: HttpHandler): EmbedDocumentQuery {
  return EmbedDocumentQuery { query ->
    val request = OpenAiEmbeddingsRequest(listOf(query.value.value))
    client.fetchDocumentQueryEmbeddings(request)
      .map { response ->
        EmbeddedQuery(response.embedding(), query.resultsCount)
      }
  }
}

private fun OpenAiEmbeddingsResponse.embedding(): List<Double> = data.first().embedding

private fun HttpHandler.fetchDocumentQueryEmbeddings(
  request: OpenAiEmbeddingsRequest
): Result<OpenAiEmbeddingsResponse, EmbedDocumentQueryError> =
  fetchEmbeddings(request).mapFailure { EmbedDocumentQueryError(it.message) }

fun EmbedDocumentQuery.Companion.withOpenAi(client: HttpHandler) = OpenAiEmbedDocumentQuery(client)
