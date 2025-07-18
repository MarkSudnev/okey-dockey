package pl.sudneu.purple.domain

import dev.forkhandles.result4k.Result
import java.net.URI

fun interface FetchDocument {
  operator fun invoke(uri: URI): Result<Unit, PurpleError>

  companion object
}
