package pl.sudneu.purple.domain.store

import dev.forkhandles.result4k.Result
import pl.sudneu.purple.domain.PurpleError

fun interface StoreDocument {
  operator fun invoke(document: EmbeddedDocument): Result<Unit, PurpleError.StoreDocumentError>

  companion object
}
