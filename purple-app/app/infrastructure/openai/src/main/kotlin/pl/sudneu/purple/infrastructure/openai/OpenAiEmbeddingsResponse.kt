package pl.sudneu.purple.infrastructure.openai

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class OpenAiEmbeddingsResponse(
  val model: String,
  val `object`: String,
  val data: List<OpenAiEmbeddingsData>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class OpenAiEmbeddingsData(
  val embedding: List<Double>,
  val index: Long,
  val `object`: String
)
