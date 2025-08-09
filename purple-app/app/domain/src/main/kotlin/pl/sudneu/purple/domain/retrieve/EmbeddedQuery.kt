package pl.sudneu.purple.domain.retrieve

import pl.sudneu.purple.shared.PositiveInteger

data class EmbeddedQuery(val embedding: List<Double>, val resultsCount: PositiveInteger)
