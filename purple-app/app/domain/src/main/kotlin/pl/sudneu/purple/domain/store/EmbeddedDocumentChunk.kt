package pl.sudneu.purple.domain.store

import pl.sudneu.purple.shared.NonBlankString

data class EmbeddedDocument(val chunks: List<EmbeddedDocumentChunk>)

data class EmbeddedDocumentChunk(val content: NonBlankString, val embeddings: List<Double>) {
  constructor(content: String, embeddings: List<Double>):
    this(NonBlankString(content), embeddings)
}
