package pl.sudneu.purple.domain.retrieve

import dev.forkhandles.result4k.Result
import pl.sudneu.purple.domain.PurpleError

fun interface EmbedDocumentQuery {
  operator fun invoke(query: DocumentQuery): Result<EmbeddedQuery, PurpleError.EmbedDocumentQueryError>

  companion object
}
