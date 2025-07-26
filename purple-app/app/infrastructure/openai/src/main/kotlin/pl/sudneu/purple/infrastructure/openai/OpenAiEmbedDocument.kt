package pl.sudneu.purple.infrastructure.openai

import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.asFailure
import dev.forkhandles.result4k.asSuccess
import dev.forkhandles.result4k.flatMap
import dev.forkhandles.result4k.map
import dev.forkhandles.result4k.mapFailure
import dev.forkhandles.result4k.zip
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.with
import pl.sudneu.purple.domain.Document
import pl.sudneu.purple.domain.EmbedDocument
import pl.sudneu.purple.domain.EmbeddedDocument
import pl.sudneu.purple.domain.EmbeddedDocumentChunk
import pl.sudneu.purple.domain.PurpleError

fun OpenAiEmbedDocument(client: HttpHandler, splitDocument: SplitDocument): EmbedDocument =
  EmbedDocument { document: Document ->
    val documentChunks = splitDocument(document)
    val embeddings = documentChunks
      .map { texts -> OpenAiEmbeddingsRequest(texts) }
      .flatMap { request -> client.getEmbeddings(request) }
      .map { embeddingsResponse -> embeddingsResponse.data.map { it.embedding } }

    zip(documentChunks, embeddings) { chunks, embs ->
      chunks.mapIndexed { index, chunk -> EmbeddedDocumentChunk(chunk, embs[index]) }
    }
      .map { EmbeddedDocument(it) }
      .mapFailure { PurpleError.EmbedDocumentError(it.message) }
  }

private fun HttpHandler.getEmbeddings(
  request: OpenAiEmbeddingsRequest
): Result<OpenAiEmbeddingsResponse, PurpleError.EmbedDocumentError> {
  val response = this(
    Request(GET, "/v1/embeddings")
      .with(openAiRequestBodyLens of request)
  )
  return if
    (response.status.successful) openAiResponseBodyLens(response).asSuccess()
  else PurpleError.EmbedDocumentError(response.bodyString()).asFailure()
}
