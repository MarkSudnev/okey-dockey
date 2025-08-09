package pl.sudneu.purple.domain.store

import dev.forkhandles.result4k.Result
import pl.sudneu.purple.domain.Document
import pl.sudneu.purple.domain.PurpleError

fun interface EmbedDocument {
  operator fun invoke(document: Document): Result<EmbeddedDocument, PurpleError.EmbedDocumentError>

  companion object
}
