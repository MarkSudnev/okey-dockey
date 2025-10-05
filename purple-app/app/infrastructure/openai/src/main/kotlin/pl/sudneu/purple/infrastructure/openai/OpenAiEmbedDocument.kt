package pl.sudneu.purple.infrastructure.openai

import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.flatMap
import dev.forkhandles.result4k.map
import dev.forkhandles.result4k.mapFailure
import dev.forkhandles.result4k.zip
import org.http4k.core.HttpHandler
import pl.sudneu.purple.domain.Document
import pl.sudneu.purple.domain.PurpleError
import pl.sudneu.purple.domain.handleException
import pl.sudneu.purple.domain.store.EmbedDocument
import pl.sudneu.purple.domain.store.EmbeddedDocument
import pl.sudneu.purple.domain.store.EmbeddedDocumentChunk

fun OpenAiEmbedDocument(client: HttpHandler, splitDocument: SplitDocument): EmbedDocument =

  EmbedDocument { document: Document ->
    val documentChunks = handleException { splitDocument(document) }
    val embeddings = documentChunks
      .map { texts -> OpenAiEmbeddingsRequest(texts) }
      .flatMap { request -> client.fetchDocumentEmbeddings(request) }
      .map { embeddingsResponse -> embeddingsResponse.data.map { it.embedding } }

    zip(documentChunks, embeddings) { chunks, embs ->
      chunks.mapIndexed { index, chunk -> EmbeddedDocumentChunk(chunk, embs[index]) }
    }
      .map { chunks -> EmbeddedDocument(document.filename, chunks) }
      .mapFailure { error -> PurpleError.EmbedDocumentError(error.message) }
  }

private fun HttpHandler.fetchDocumentEmbeddings(
  request: OpenAiEmbeddingsRequest
): Result<OpenAiEmbeddingsResponse, PurpleError.EmbedDocumentError> =
  fetchEmbeddings(request).mapFailure { error ->
    PurpleError.EmbedDocumentError(error.message)
  }

fun EmbedDocument.Companion.withOpenAi(
  client: HttpHandler,
  splitDocument: SplitDocument
) = OpenAiEmbedDocument(client, splitDocument)
