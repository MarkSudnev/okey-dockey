package pl.sudneu.purple.domain

import dev.forkhandles.result4k.Result
import pl.sudneu.purple.domain.PurpleError.FetchDocumentError

fun interface FetchDocument {
  operator fun invoke(uri: RemoteFileLocation): Result<Document, FetchDocumentError>

  companion object
}
