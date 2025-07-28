package pl.sudneu.purple.infrastructure.openai

import dev.forkhandles.fabrikate.Fabrikate
import dev.forkhandles.result4k.kotest.shouldBeSuccess
import org.junit.jupiter.api.Test
import pl.sudneu.purple.domain.Document

class SplitDocumentPlaceholderShould {

  @Test
  fun `wrap document content with list`() {
    val document: Document = Fabrikate().random()
    val splitter = SplitDocument.placeholder()
    splitter(document) shouldBeSuccess listOf(document.content.value)
  }
}
