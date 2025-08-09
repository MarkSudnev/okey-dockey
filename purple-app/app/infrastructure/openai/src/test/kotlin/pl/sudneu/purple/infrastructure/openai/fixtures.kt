package pl.sudneu.purple.infrastructure.openai

internal fun openAiResponseBody(): String =
  ClassLoader.getSystemResource("open-ai-embeddings-response.json").readText()
