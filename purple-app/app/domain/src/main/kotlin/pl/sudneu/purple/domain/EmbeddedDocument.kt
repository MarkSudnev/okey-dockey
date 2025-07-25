package pl.sudneu.purple.domain

import java.util.Vector

data class EmbeddedDocument(val content: NonBlankString, val embeddings: Vector<Double>) {
  constructor(content: String, embeddings: Vector<Double>):
    this(NonBlankString(content), embeddings)
}
