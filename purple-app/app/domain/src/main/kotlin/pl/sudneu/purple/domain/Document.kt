package pl.sudneu.purple.domain

data class Document(val content: NonBlankString) {
  constructor(content: String) : this(NonBlankString(content))
}
