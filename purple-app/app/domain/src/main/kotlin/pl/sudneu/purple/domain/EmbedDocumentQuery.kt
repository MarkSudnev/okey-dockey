package pl.sudneu.purple.domain

import dev.forkhandles.result4k.Result

fun interface EmbedDocumentQuery {
  operator fun invoke(query: DocumentQuery): Result<EmbeddedQuery, PurpleError.EmbedDocumentQueryError>

  companion object
}
