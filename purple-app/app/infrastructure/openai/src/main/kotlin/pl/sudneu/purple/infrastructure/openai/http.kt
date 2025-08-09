package pl.sudneu.purple.infrastructure.openai

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.asFailure
import dev.forkhandles.result4k.asSuccess
import dev.forkhandles.result4k.flatMap
import dev.forkhandles.result4k.mapFailure
import dev.forkhandles.result4k.resultFrom
import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.with
import org.http4k.format.Jackson.auto
import pl.sudneu.purple.domain.PurpleError
import pl.sudneu.purple.domain.toPurpleMessage

internal val openAiRequestBodyLens = Body.auto<OpenAiEmbeddingsRequest>().toLens()
internal val openAiResponseBodyLens = Body.auto<OpenAiEmbeddingsResponse>().toLens()

internal fun HttpHandler.fetchEmbeddings(
  request: OpenAiEmbeddingsRequest
): Result<OpenAiEmbeddingsResponse, OpenAiError> =
  resultFrom {
    this(Request(POST, "/v1/embeddings").with(openAiRequestBodyLens of request))
  }
    .mapFailure { e -> OpenAiError(e.toPurpleMessage()) }
    .flatMap { response ->
      if (response.status.successful) openAiResponseBodyLens(response).asSuccess()
      else OpenAiError(response.bodyString()).asFailure()
    }

internal data class OpenAiError(val message: String)

internal data class OpenAiEmbeddingsRequest(val input: List<String>)

@JsonIgnoreProperties(ignoreUnknown = true)
internal data class OpenAiEmbeddingsResponse(
  val model: String,
  val `object`: String,
  val data: List<OpenAiEmbeddingsData>
)

@JsonIgnoreProperties(ignoreUnknown = true)
internal data class OpenAiEmbeddingsData(
  val embedding: List<Double>,
  val index: Long,
  val `object`: String
)
