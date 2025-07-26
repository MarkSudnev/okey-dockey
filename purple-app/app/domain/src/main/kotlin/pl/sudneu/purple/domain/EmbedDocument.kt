package pl.sudneu.purple.domain

import dev.forkhandles.result4k.Result
import pl.sudneu.purple.domain.PurpleError.EmbedDocumentError

fun interface EmbedDocument {
  operator fun invoke(document: Document): Result<List<EmbeddedDocument>, EmbedDocumentError>

  companion object
}
