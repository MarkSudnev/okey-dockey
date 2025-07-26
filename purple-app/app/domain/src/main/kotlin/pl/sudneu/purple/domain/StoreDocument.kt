package pl.sudneu.purple.domain

import dev.forkhandles.result4k.Result

fun interface StoreDocument {
  operator fun invoke(document: EmbeddedDocument): Result<Unit, PurpleError.StoreDocumentError>

  companion object
}
