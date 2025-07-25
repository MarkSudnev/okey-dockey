package pl.sudneu.purple.infrastructure.openai

data class OpenAiEmbeddingsRequest(val input: String) {
  init {
    require(input.isNotEmpty()) { "empty input" }
    require(input.isNotBlank()) { "blank input" }
  }
}
