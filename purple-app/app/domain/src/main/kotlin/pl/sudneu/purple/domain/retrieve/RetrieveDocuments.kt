package pl.sudneu.purple.domain.retrieve

import dev.forkhandles.result4k.Result
import pl.sudneu.purple.domain.Document
import pl.sudneu.purple.domain.PurpleError.RetrieveDocumentsError


fun interface RetrieveDocuments {
  operator fun invoke(query: EmbeddedQuery): Result<List<Document>, RetrieveDocumentsError>

  companion object
}
