package pl.sudneu.purple.domain

import io.kotest.assertions.throwables.shouldThrow
import org.junit.jupiter.api.Test
import pl.sudneu.purple.domain.store.RemoteFileLocation
import java.net.URI

class RemoteFileLocationShould {

  @Test
  fun `throw when uri is blank string`() {
     shouldThrow<IllegalArgumentException> {
         RemoteFileLocation(URI.create("  "))
     }
  }

  @Test
  fun `throw when uri is empty string`() {
     shouldThrow<IllegalArgumentException> {
         RemoteFileLocation(URI.create(""))
     }
  }
}
