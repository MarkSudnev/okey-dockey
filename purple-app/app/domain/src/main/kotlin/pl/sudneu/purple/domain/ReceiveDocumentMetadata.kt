package pl.sudneu.purple.domain

import dev.forkhandles.result4k.Result

fun interface ReceiveDocumentMetadata {
  operator fun invoke(metadata: DocumentMetadata): Result<Unit, PurpleError>

  companion object
}
