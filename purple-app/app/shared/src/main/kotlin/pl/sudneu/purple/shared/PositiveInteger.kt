package pl.sudneu.purple.shared

data class PositiveInteger(val value: Int) {
  init {
    require(value > 0) { "Value must be positive" }
  }
}

