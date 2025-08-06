package pl.sudneu.purple.domain

import dev.forkhandles.result4k.Result

typealias Embedding = List<Double>

fun interface RetrieveDocuments {
  operator fun invoke(embedding: Embedding): Result<List<String>, PurpleError.RetrieveDocumentsError>

  companion object
}
