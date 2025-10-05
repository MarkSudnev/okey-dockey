package pl.sudneu.purple.domain

import pl.sudneu.purple.shared.NonBlankString

data class Document(val filename: NonBlankString, val content: NonBlankString) {
  constructor(filename: String, content: String) :
    this(NonBlankString(filename), NonBlankString(content))
}
