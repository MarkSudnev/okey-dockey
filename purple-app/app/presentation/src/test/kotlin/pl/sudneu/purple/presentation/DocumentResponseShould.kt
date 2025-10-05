package pl.sudneu.purple.presentation

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import pl.sudneu.purple.domain.Document

class DocumentResponseShould {

  @Test
  fun `be created from document`() {
    val document = Document(
      "alpha.txt",
      "Enim suscipit platea pharetra mi malesuada ad."
    )
    with(document.toDocumentResponse()) {
      filename shouldBe document.filename.value
      content shouldBe document.content.value
    }
  }
}
