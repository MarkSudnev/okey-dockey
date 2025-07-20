package pl.sudneu.purple.domain

data class NonBlankString(val value: String) {
  init {
    require(value.isNotEmpty())
    require(value.isNotBlank())
  }
}
