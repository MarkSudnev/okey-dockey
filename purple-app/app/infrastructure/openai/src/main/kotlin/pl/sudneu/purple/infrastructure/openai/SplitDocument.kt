package pl.sudneu.purple.infrastructure.openai

import dev.forkhandles.result4k.Result
import pl.sudneu.purple.domain.Document
import pl.sudneu.purple.domain.PurpleError

fun interface SplitDocument {
  operator fun invoke(document: Document): Result<List<String>, PurpleError>

  companion object
}
