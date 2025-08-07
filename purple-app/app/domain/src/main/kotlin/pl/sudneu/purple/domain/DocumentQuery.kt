package pl.sudneu.purple.domain

data class DocumentQuery(val value: NonBlankString) {
  constructor(input: String): this(NonBlankString(input))
  override fun toString() = value.value
}
