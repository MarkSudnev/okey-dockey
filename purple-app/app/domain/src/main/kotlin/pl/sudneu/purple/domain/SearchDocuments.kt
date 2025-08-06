package pl.sudneu.purple.domain

import dev.forkhandles.result4k.Result
import pl.sudneu.purple.domain.PurpleError.SearchDocumentError

fun interface SearchDocuments {
  operator fun invoke(keyword: NonBlankString): Result<List<Document>, SearchDocumentError>

  companion object
}
