package pl.sudneu.purple.shared

data class NonBlankString(val value: String) {
  init {
    require(value.isNotEmpty())
    require(value.isNotBlank())
  }
}
