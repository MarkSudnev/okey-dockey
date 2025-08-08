package pl.sudneu.purple.domain

import pl.sudneu.purple.shared.PositiveInteger

data class EmbeddedQuery(val embedding: List<Double>, val resultsCount: PositiveInteger)
