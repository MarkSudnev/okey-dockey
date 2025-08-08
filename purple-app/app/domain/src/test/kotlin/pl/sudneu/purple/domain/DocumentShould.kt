package pl.sudneu.purple.domain

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class DocumentShould {

  @Test
  fun `create instance from string`() {
    Document("Lorem Ipsum").content.value shouldBe "Lorem Ipsum"
  }
}
