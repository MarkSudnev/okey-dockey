package pl.sudneu.purple.domain.store

import java.net.URI

data class RemoteFileLocation(val uri: URI) {
    init {
        require(uri.toString().isNotEmpty())
    }
}
