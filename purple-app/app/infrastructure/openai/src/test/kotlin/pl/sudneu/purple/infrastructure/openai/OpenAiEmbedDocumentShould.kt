package pl.sudneu.purple.infrastructure.openai

import dev.forkhandles.result4k.asSuccess
import dev.forkhandles.result4k.kotest.shouldBeSuccess
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.junit.jupiter.api.Test
import pl.sudneu.purple.domain.Document
import pl.sudneu.purple.domain.EmbedDocument
import pl.sudneu.purple.domain.EmbeddedDocument
import pl.sudneu.purple.domain.EmbeddedDocumentChunk

class OpenAiEmbedDocumentShould {

  @Test
  fun `get embeddings from open ai`() {
    val httpClient: HttpHandler = { Response(OK).body(openAiResponseBody()) }
    val document = Document("Hac vel parturient consectetur diam porta.")
    val embedDocument = OpenAiEmbedDocument(httpClient)

    embedDocument(document) shouldBeSuccess { embeddedDocument ->
      embeddedDocument.chunks shouldHaveSize 1
      embeddedDocument.chunks.first().content shouldBe document.content
      embeddedDocument.chunks.first().embeddings shouldHaveSize 1152
    }
  }
}

fun openAiResponseBody(): String =
  ClassLoader.getSystemResource("open-ai-embeddings-response.json").readText()

fun OpenAiEmbedDocument(client: HttpHandler): EmbedDocument =
  EmbedDocument { document: Document ->
    val openAiResponse = client(Request(GET, "/v1/embeddings"))
    val embeddings = openAiResponseBodyLens(openAiResponse)
    EmbeddedDocument(embeddings.data.map {
      EmbeddedDocumentChunk(document.content, it.embedding)
    }).asSuccess()
  }
