package pl.sudneu.purple.infrastructure.openai

import dev.forkhandles.result4k.asSuccess
import dev.forkhandles.result4k.kotest.shouldBeSuccess
import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.junit.jupiter.api.Test
import pl.sudneu.purple.domain.Document
import pl.sudneu.purple.domain.EmbedDocument
import pl.sudneu.purple.domain.EmbeddedDocument
import java.util.*

class OpenAiEmbedDocumentShould {

  @Test
  fun `get embeddings from open ai`() {
    val httpClient: HttpHandler = { Response(OK).body(openAiResponseBody()) }
    val document = Document("Hac vel parturient consectetur diam porta.")
    val embedDocument = OpenAiEmbedDocument()
    embedDocument(document) shouldBeSuccess EmbeddedDocument(document.content, Vector<Double>(10))
  }
}

fun openAiResponseBody(): String =
  ClassLoader.getSystemResource("open-ai-embeddings-response.json").readText()

fun OpenAiEmbedDocument(): EmbedDocument =
  EmbedDocument { document: Document ->
    EmbeddedDocument(document.content, Vector<Double>(10)).asSuccess()
  }
