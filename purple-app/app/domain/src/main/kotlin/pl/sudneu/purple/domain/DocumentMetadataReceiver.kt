package pl.sudneu.purple.domain

import dev.forkhandles.result4k.flatMap

fun DocumentMetadataReceiver(
  fetchDocument: FetchDocument,
  embedDocument: EmbedDocument,
  storeDocument: StoreDocument
): ReceiveDocumentMetadata {

  return ReceiveDocumentMetadata { metadata ->
    fetchDocument(metadata.toRemoteFileLocation())
      .flatMap { document -> embedDocument(document) }
      .flatMap { embeddedDocument -> storeDocument(embeddedDocument) }
  }
}
