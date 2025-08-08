package pl.sudneu.purple.domain

import dev.forkhandles.result4k.Result

fun interface SearchDocuments {
  operator fun invoke(query: DocumentQuery): Result<List<Document>, PurpleError>

  companion object
}
