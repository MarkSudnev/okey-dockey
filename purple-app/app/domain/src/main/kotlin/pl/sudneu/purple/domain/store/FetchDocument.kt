package pl.sudneu.purple.domain.store

import dev.forkhandles.result4k.Result
import pl.sudneu.purple.domain.Document
import pl.sudneu.purple.domain.PurpleError

fun interface FetchDocument {
  operator fun invoke(fileLocation: RemoteFileLocation): Result<Document, PurpleError.FetchDocumentError>

  companion object
}
