package pl.sudneu.purple.infrastructure.openai

import dev.forkhandles.result4k.asFailure
import dev.forkhandles.result4k.asSuccess
import dev.forkhandles.result4k.kotest.shouldBeFailure
import dev.forkhandles.result4k.kotest.shouldBeSuccess
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Status.Companion.INTERNAL_SERVER_ERROR
import org.http4k.core.Status.Companion.OK
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import pl.sudneu.purple.domain.Document
import pl.sudneu.purple.domain.PurpleError.EmbedDocumentError

class OpenAiEmbedDocumentShould {

  @Test
  fun `get embeddings from open ai`() {
    val httpClient: HttpHandler = { Response(OK).body(openAiResponseBody()) }
    val document = Document("alpha.txt","Hac vel parturient consectetur diam porta.",)
    val embedDocument = OpenAiEmbedDocument(httpClient, DummyDocumentSplitter())

    embedDocument(document) shouldBeSuccess { embeddedDocument ->
      embeddedDocument.filename shouldBe document.filename
      embeddedDocument.chunks shouldHaveSize 2
      embeddedDocument.chunks.first().content shouldBe document.content
      embeddedDocument.chunks.first().embeddings shouldHaveSize 1152
      embeddedDocument.chunks.last().embeddings shouldHaveSize 1152
    }
  }

  @ParameterizedTest
  @ValueSource(ints = [400, 401, 402, 403, 404, 500, 501, 502, 503])
  fun `return failure when http request was not successful`(statusCode: Int) {
    val status = Status.fromCode(statusCode) ?: INTERNAL_SERVER_ERROR
    val httpClient: HttpHandler = { Response(status).body("Server error") }
    val document = Document("alpha.txt", "Hac vel parturient consectetur diam porta.",)
    val embedDocument = OpenAiEmbedDocument(httpClient, DummyDocumentSplitter())

    embedDocument(document) shouldBeFailure EmbedDocumentError("Server error")
  }

  @Test
  fun `return failure when text splitting was failed`() {
    val httpClient: HttpHandler = { Response(OK).body(openAiResponseBody()) }
    val document = Document("alpha.txt", "Hac vel parturient consectetur diam porta.",)
    val embedDocument = OpenAiEmbedDocument(httpClient) {
      EmbedDocumentError("some-error").asFailure()
    }
    embedDocument(document) shouldBeFailure EmbedDocumentError("some-error")
  }

  @Test
  fun `return failure when text splitting throws an exception`() {
    val httpClient: HttpHandler = { Response(OK).body(openAiResponseBody()) }
    val document = Document("alpha.txt", "Hac vel parturient consectetur diam porta.",)
    val embedDocument = OpenAiEmbedDocument(httpClient) {
      error("unexpected exception")
    }
    embedDocument(document) shouldBeFailure EmbedDocumentError("IllegalStateException: unexpected exception")
  }

  @Test
  fun `return failure when http client throws exception`() {
    val httpClient: HttpHandler = { error("unexpected exception") }
    val document = Document("alpha.txt", "Hac vel parturient consectetur diam porta.",)
    val embedDocument = OpenAiEmbedDocument(httpClient, DummyDocumentSplitter())

    embedDocument(document) shouldBeFailure EmbedDocumentError("IllegalStateException: unexpected exception")
  }
}

private fun DummyDocumentSplitter(): SplitDocument =
  SplitDocument { document: Document ->  listOf(document.content.value, document.content.value).asSuccess() }

