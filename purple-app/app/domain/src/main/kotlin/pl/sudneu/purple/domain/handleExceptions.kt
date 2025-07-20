package pl.sudneu.purple.domain

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import pl.sudneu.purple.domain.PurpleError.UnexpectedError

fun <T> handleException(block: (Unit) -> Result<T, PurpleError>): Result<T, PurpleError> {
  return try {
    block(Unit)
  } catch (e: Throwable) {
    Failure(UnexpectedError("${e::class.simpleName}: ${e.message}"))
  }
}
