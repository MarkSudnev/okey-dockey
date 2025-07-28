package pl.sudneu.purple.infrastructure.openai

import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.asSuccess
import pl.sudneu.purple.domain.Document
import pl.sudneu.purple.domain.PurpleError

fun interface SplitDocument {
  operator fun invoke(document: Document): Result<List<String>, PurpleError>

  companion object
}

fun SplitDocument.Companion.placeholder(): SplitDocument =
  SplitDocument { listOf(it.content.value).asSuccess() }
