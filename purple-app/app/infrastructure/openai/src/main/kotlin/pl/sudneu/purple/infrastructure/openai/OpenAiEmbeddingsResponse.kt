package pl.sudneu.purple.infrastructure.openai

data class OpenAiEmbeddingsResponse(
  val model: String,
  val `object`: String,
  val data: List<OpenAiEmbeddingsData>
)

data class OpenAiEmbeddingsData(
  val embedding: List<Double>,
  val index: Long,
  val `object`: String
)
