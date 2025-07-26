package pl.sudneu.purple.domain

import dev.forkhandles.result4k.Result
import pl.sudneu.purple.domain.PurpleError.StoreDocumentError

fun interface StoreDocument {
  operator fun invoke(document: List<EmbeddedDocument>): Result<Unit, StoreDocumentError>

  companion object
}
