package pl.sudneu.purple.domain

import pl.sudneu.purple.shared.NonBlankString

data class DocumentQuery(val value: NonBlankString) {
  constructor(input: String): this(NonBlankString(input))
  override fun toString() = value.value
}
