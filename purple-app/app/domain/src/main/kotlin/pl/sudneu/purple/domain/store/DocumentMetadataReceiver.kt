package pl.sudneu.purple.domain.store

import dev.forkhandles.result4k.flatMap
import pl.sudneu.purple.domain.FetchDocument
import pl.sudneu.purple.domain.handleException

fun DocumentMetadataReceiver(
    fetchDocument: FetchDocument,
    embedDocument: EmbedDocument,
    storeDocument: StoreDocument
): ReceiveDocumentMetadata =
  ReceiveDocumentMetadata { metadata ->
      handleException {
          fetchDocument(metadata.fileLocation)
              .flatMap(embedDocument::invoke)
              .flatMap(storeDocument::invoke)
      }
  }
