package pl.sudneu.purple.presentation

import pl.sudneu.purple.domain.Document

data class DocumentResponse(val filename: String, val content: String)

internal fun Document.toDocumentResponse(): DocumentResponse =
  DocumentResponse(filename.value, content.value)
