package pl.sudneu.purple.domain

import dev.forkhandles.result4k.flatMap

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
