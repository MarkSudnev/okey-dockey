package pl.sudneu.purple.domain

data class EmbeddedDocument(val content: NonBlankString, val embeddings: List<Double>) {
  constructor(content: String, embeddings: List<Double>):
    this(NonBlankString(content), embeddings)
}
