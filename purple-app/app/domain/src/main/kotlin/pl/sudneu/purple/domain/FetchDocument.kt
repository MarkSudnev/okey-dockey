package pl.sudneu.purple.domain

import dev.forkhandles.result4k.Result
import pl.sudneu.purple.domain.PurpleError.FetchDocumentError
import pl.sudneu.purple.domain.store.RemoteFileLocation

fun interface FetchDocument {
  operator fun invoke(fileLocation: RemoteFileLocation): Result<Document, FetchDocumentError>

  companion object
}
