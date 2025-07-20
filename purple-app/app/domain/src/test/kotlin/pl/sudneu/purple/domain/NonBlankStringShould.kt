package pl.sudneu.purple.domain

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import org.junit.jupiter.api.Test

class NonBlankStringShould {

  @Test
  fun `accept normal string`() {
    shouldNotThrowAny {
      NonBlankString("normal string")
    }
  }

  @Test
  fun `throw when empty string is passed`() {
    shouldThrow<IllegalArgumentException> {
      NonBlankString("")
    }
  }

  @Test
  fun `throw when blank string is passed`() {
    shouldThrow<IllegalArgumentException> {
      NonBlankString("    ")
    }
  }
}
