package pl.sudneu.purple.domain.retrieve

import dev.forkhandles.result4k.flatMap
import pl.sudneu.purple.domain.handleException

fun DocumentFinder(
  embedDocumentQuery: EmbedDocumentQuery,
  retrieveDocuments: RetrieveDocuments
): SearchDocuments =
  SearchDocuments { query->
    handleException { embedDocumentQuery(query).flatMap(retrieveDocuments::invoke) }
  }
