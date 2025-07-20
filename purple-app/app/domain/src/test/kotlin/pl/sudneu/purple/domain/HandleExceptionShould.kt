package pl.sudneu.purple.domain

import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.asFailure
import dev.forkhandles.result4k.asSuccess
import dev.forkhandles.result4k.kotest.shouldBeFailure
import dev.forkhandles.result4k.kotest.shouldBeSuccess
import org.junit.jupiter.api.Test
import pl.sudneu.purple.domain.PurpleError.UnexpectedError
import pl.sudneu.purple.domain.PurpleError.UnknownError

class HandleExceptionShould {

  @Test
  fun `return unexpected error failure when exception is happened`() {
    val block: (Unit) -> Result<Unit, PurpleError> = { error("exception happened") }
    handleException(block).shouldBeFailure(UnexpectedError("IllegalStateException: exception happened"))
  }

  @Test
  fun `return failure from block when no exception happened`() {
    val block: (Unit) -> Result<Unit, PurpleError> = { UnknownError.asFailure() }
    handleException(block).shouldBeFailure(UnknownError)
  }

  @Test
  fun `return success when block was succeed`() {
    val block: (Unit) -> Result<Unit, PurpleError> = { Unit.asSuccess() }
    handleException(block).shouldBeSuccess(Unit)
  }
}
