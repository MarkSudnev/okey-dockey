package pl.sudneu.purple.domain

import dev.forkhandles.result4k.Result

fun interface SearchDocuments {
  operator fun invoke(query: DocumentQuery, resultCount: Int): Result<List<Document>, PurpleError>

  companion object
}
