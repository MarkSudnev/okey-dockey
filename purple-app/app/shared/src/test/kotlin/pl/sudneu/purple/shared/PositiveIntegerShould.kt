package pl.sudneu.purple.shared

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import org.junit.jupiter.api.Test

class PositiveIntegerShould {

  @Test
  fun `accept positive number`() {
    shouldNotThrowAny { PositiveInteger(42) }
  }

  @Test
  fun `throw when zero is passed`() {
    shouldThrow<IllegalArgumentException> {
      PositiveInteger(0)
    }
  }

  @Test
  fun `throw when negative number is passed`() {
    shouldThrow<IllegalArgumentException> {
      PositiveInteger(-100)
    }
  }
}
