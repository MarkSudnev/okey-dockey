package pl.sudneu.purple.domain

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import org.junit.jupiter.api.Test

class NonBlankStringTest {

  @Test
  fun `accepts normal string`() {
    shouldNotThrowAny {
      NonBlankString("normal string")
    }
  }

  @Test
  fun `throws when empty string is passed`() {
    shouldThrow<IllegalArgumentException> {
      NonBlankString("")
    }
  }

  @Test
  fun `throws when blank string is passed`() {
    shouldThrow<IllegalArgumentException> {
      NonBlankString("    ")
    }
  }
}
