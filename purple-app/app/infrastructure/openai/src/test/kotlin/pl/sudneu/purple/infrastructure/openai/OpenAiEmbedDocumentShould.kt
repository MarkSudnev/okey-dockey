package pl.sudneu.purple.infrastructure.openai

import dev.forkhandles.result4k.asSuccess
import org.junit.jupiter.api.Test
import pl.sudneu.purple.domain.EmbedDocument
import pl.sudneu.purple.domain.EmbeddedDocument
import java.util.Vector

class OpenAiEmbedDocumentShould {

  @Test
  fun `get embeddings from open ai`() {

  }
}

fun OpenAiEmbedDocument(): EmbedDocument =
  EmbedDocument {
    EmbeddedDocument("Hello", Vector<Double>(10)).asSuccess()
  }
