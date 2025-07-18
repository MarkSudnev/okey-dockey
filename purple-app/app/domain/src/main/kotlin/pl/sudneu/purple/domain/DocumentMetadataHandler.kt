package pl.sudneu.purple.domain

import dev.forkhandles.result4k.Success

fun DocumentMetadataHandler(): ReceiveDocumentMetadata {

  return ReceiveDocumentMetadata { Success(Unit) }
}
