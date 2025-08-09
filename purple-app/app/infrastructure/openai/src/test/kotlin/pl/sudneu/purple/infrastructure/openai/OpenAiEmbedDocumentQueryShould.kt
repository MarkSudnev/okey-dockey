package pl.sudneu.purple.infrastructure.openai

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
import pl.sudneu.purple.domain.PurpleError.EmbedDocumentQueryError
import pl.sudneu.purple.domain.retrieve.DocumentQuery

class OpenAiEmbedDocumentQueryShould {

  @Test
  fun `get embeddings from open ai`() {
    val httpClient: HttpHandler = { Response(OK).body(openAiResponseBody()) }
    val documentQuery = DocumentQuery("Consequat ante tempor vestibulum platea.", 1)
    val embedQuery = OpenAiEmbedDocumentQuery(httpClient)

    embedQuery(documentQuery) shouldBeSuccess { embeddedQuery ->
      embeddedQuery.embedding shouldHaveSize 1152
      embeddedQuery.resultsCount.value shouldBe 1
    }
  }

  @ParameterizedTest
  @ValueSource(ints = [400, 401, 402, 403, 404, 500, 501, 502, 503])
  fun `return failure when http request was not successful`(statusCode: Int) {
    val status = Status.fromCode(statusCode) ?: INTERNAL_SERVER_ERROR
    val httpClient: HttpHandler = { Response(status).body("Server error") }
    val documentQuery = DocumentQuery("Consequat ante tempor vestibulum platea.", 1)

    val embedQuery = OpenAiEmbedDocumentQuery(httpClient)
    embedQuery(documentQuery) shouldBeFailure EmbedDocumentQueryError("Server error")
  }

  @Test
  fun `return failure when http client throws exception`() {
    val httpClient: HttpHandler = { error("unexpected exception") }

    val documentQuery = DocumentQuery("Consequat ante tempor vestibulum platea.", 1)

    val embedQuery = OpenAiEmbedDocumentQuery(httpClient)
    embedQuery(documentQuery) shouldBeFailure
      EmbedDocumentQueryError("IllegalStateException: unexpected exception")
  }
}

