package pl.sudneu.purple.domain

import dev.forkhandles.result4k.flatMap
import pl.sudneu.purple.domain.store.EmbedDocument
import pl.sudneu.purple.domain.store.ReceiveDocumentMetadata
import pl.sudneu.purple.domain.store.StoreDocument

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
