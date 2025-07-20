package pl.sudneu.purple.domain

import java.net.URI

data class RemoteFileLocation(val uri: URI)

fun DocumentMetadata.toRemoteFileLocation(): RemoteFileLocation =
  RemoteFileLocation(URI(filePath))
