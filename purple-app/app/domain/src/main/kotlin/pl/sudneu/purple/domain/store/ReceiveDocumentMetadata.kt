package pl.sudneu.purple.domain.store

import dev.forkhandles.result4k.Result
import pl.sudneu.purple.domain.PurpleError

fun interface ReceiveDocumentMetadata {
  operator fun invoke(metadata: DocumentMetadata): Result<Unit, PurpleError>

  companion object
}
