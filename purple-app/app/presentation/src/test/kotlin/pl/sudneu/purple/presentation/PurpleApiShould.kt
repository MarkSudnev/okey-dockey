package pl.sudneu.purple.presentation

import com.zaxxer.hikari.HikariDataSource
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.kotest.shouldNotHaveStatus
import org.junit.jupiter.api.Test

class PurpleApiShould {

  private val api = PurpleApi(
    HikariDataSource(testEnvironment.toHikariConfig())
  ) { Response(OK) }

  @Test
  fun `return response on search documents`() {
    api(Request(GET, "/api/v1/documents?q=hello&n=3")) shouldNotHaveStatus NOT_FOUND
  }
}
