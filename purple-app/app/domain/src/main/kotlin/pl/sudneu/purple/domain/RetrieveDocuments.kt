package pl.sudneu.purple.domain

import dev.forkhandles.result4k.Result
import pl.sudneu.purple.domain.PurpleError.RetrieveDocumentsError


fun interface RetrieveDocuments {
  operator fun invoke(query: EmbeddedQuery, resultsCount: Int): Result<List<Document>, RetrieveDocumentsError>

  companion object
}
