package pl.sudneu.purple.domain

import java.net.URI

data class RemoteFileLocation(val uri: URI) {
    init {
        require(uri.toString().isNotEmpty())
    }
}
