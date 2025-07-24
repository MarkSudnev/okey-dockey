package pl.sudneu.purple.presentation

import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.kotest.shouldNotHaveStatus
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class PurpleApiShould {

  private val api = PurpleApi()

  @Test
  @Disabled
  fun `return response on search documents`() {
    api(Request(GET, "/api/v1/documents")) shouldNotHaveStatus NOT_FOUND
  }
}
