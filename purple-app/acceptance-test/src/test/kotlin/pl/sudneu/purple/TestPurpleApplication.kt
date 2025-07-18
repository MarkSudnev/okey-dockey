package pl.sudneu.purple

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.forkhandles.fabrikate.Fabrikate
import dev.forkhandles.result4k.kotest.shouldBeSuccess
import io.kotest.matchers.shouldBe
import org.http4k.client.OkHttp
import org.http4k.config.Environment
import org.http4k.config.Port
import org.http4k.config.Secret
import org.http4k.core.HttpHandler
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.ClientFilters
import org.http4k.server.Http4kServer
import org.http4k.server.SunHttp
import org.http4k.server.asServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import pl.sudneu.purple.presentation.FileReceivedEvent
import pl.sudneu.purple.presentation.PurpleApi
import pl.sudneu.purple.presentation.PurpleEnvironment.VEC_DATABASE_DRIVER
import pl.sudneu.purple.presentation.PurpleEnvironment.VEC_DATABASE_PASSWORD
import pl.sudneu.purple.presentation.PurpleEnvironment.VEC_DATABASE_URL
import pl.sudneu.purple.presentation.PurpleEnvironment.VEC_DATABASE_USERNAME
import pl.sudneu.purple.presentation.PurpleMessageHandler
import javax.sql.DataSource

val environment = Environment.defaults(
  VEC_DATABASE_DRIVER of "org.h2.Driver",
  VEC_DATABASE_URL of Uri.of("jdbc:h2:mem:purple"),
  VEC_DATABASE_USERNAME of "root",
  VEC_DATABASE_PASSWORD of Secret("root")
)

class TestPurpleApplication {

  private var server: Http4kServer = PurpleApi().asServer(SunHttp(Port.RANDOM.value))
  private var client: HttpHandler =
    ClientFilters.SetBaseUriFrom(Uri.of("http://localhost:${server.port()}"))
      .then(OkHttp())

  @BeforeEach
  fun setup() {
    server.port()
    DatabaseUtils.prepare()
  }

  @AfterEach
  fun teardown() {
    server.stop()
    DatabaseUtils.clean()
  }

  @Test
  fun `should store vectorized document`() {
    val event: FileReceivedEvent = Fabrikate().random()
    val messageHandler = PurpleMessageHandler(environment)
    messageHandler(event).shouldBeSuccess()

    val result = TestDatabaseConnection
      .dataSource
      .connection
      .prepareStatement("SELECT * FROM documents")
      .executeQuery()

    result.last()
    result.row shouldBe 1
  }
}
