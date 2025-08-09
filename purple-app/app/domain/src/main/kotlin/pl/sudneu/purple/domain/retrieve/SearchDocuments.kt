package pl.sudneu.purple.domain.retrieve

import dev.forkhandles.result4k.Result
import pl.sudneu.purple.domain.Document
import pl.sudneu.purple.domain.PurpleError

fun interface SearchDocuments {
  operator fun invoke(query: DocumentQuery): Result<List<Document>, PurpleError>

  companion object
}
