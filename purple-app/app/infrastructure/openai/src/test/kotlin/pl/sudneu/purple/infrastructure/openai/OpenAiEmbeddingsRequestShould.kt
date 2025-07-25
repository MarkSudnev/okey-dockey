package pl.sudneu.purple.infrastructure.opemai

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import org.junit.jupiter.api.Test
import pl.sudneu.purple.infrastructure.openai.OpenAiEmbeddingsRequest

class OpenAiEmbeddingsRequestShould {

  @Test
  fun `accept normal string`() {
    shouldNotThrowAny { OpenAiEmbeddingsRequest("Natoque ut vel enim nibh dui.") }
  }

  @Test
  fun `not accept empty string`() {
    shouldThrow<IllegalArgumentException> { OpenAiEmbeddingsRequest("") }
  }

  @Test
  fun `not accept blank string`() {
    shouldThrow<IllegalArgumentException> { OpenAiEmbeddingsRequest("     ") }
  }
}
