package pl.sudneu.purple.domain

import pl.sudneu.purple.shared.NonBlankString
import pl.sudneu.purple.shared.PositiveInteger

data class DocumentQuery(val value: NonBlankString, val resultsCount: PositiveInteger) {
  constructor(input: String, resultsCount: Int):
    this(NonBlankString(input), PositiveInteger(resultsCount))
  override fun toString() = value.value
}
