package pl.sudneu.purple.infrastructure.openai

import org.http4k.core.Body
import org.http4k.format.Jackson.auto

val openAiRequestBodyLens = Body.auto<OpenAiEmbeddingsRequest>().toLens()
val openAiResponseBodyLens = Body.auto<OpenAiEmbeddingsResponse>().toLens()
