package pl.sudneu.purple.domain

import pl.sudneu.purple.shared.NonBlankString

data class Document(val content: NonBlankString) {
  constructor(content: String) : this(NonBlankString(content))
}
