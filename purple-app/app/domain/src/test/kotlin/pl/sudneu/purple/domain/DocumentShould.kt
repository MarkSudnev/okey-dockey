package pl.sudneu.purple.domain

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class DocumentShould {

  @Test
  fun `create instance from strings`() {
    val document = Document("alpha.txt","Lorem Ipsum")

    document.filename.value shouldBe "alpha.txt"
    document.content.value shouldBe "Lorem Ipsum"
  }
}
