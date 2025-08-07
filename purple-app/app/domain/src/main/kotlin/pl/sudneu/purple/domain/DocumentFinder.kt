package pl.sudneu.purple.domain

import dev.forkhandles.result4k.flatMap

fun DocumentFinder(
  embedDocumentQuery: EmbedDocumentQuery,
  retrieveDocuments: RetrieveDocuments
): SearchDocuments =
  SearchDocuments { query, resultsCount ->
    handleException {
      embedDocumentQuery(query)
        .flatMap { embedding -> retrieveDocuments(embedding, resultsCount) }
    }
  }
