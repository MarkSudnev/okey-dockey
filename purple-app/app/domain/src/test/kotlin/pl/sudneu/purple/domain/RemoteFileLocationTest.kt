package pl.sudneu.purple.domain

import io.kotest.assertions.throwables.shouldThrow
import org.junit.jupiter.api.Test
import java.net.URI

class RemoteFileLocationTest {

  @Test
  fun `throws when uri is blank string`() {
     shouldThrow<IllegalArgumentException> {
       RemoteFileLocation(URI.create("  "))
     }
  }
}
