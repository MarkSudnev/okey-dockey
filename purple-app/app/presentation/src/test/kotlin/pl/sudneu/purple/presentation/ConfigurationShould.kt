package pl.sudneu.purple.presentation

import com.zaxxer.hikari.HikariConfig
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import pl.sudneu.purple.presentation.PurpleEnvironment.VEC_DATABASE_DRIVER
import pl.sudneu.purple.presentation.PurpleEnvironment.VEC_DATABASE_PASSWORD
import pl.sudneu.purple.presentation.PurpleEnvironment.VEC_DATABASE_URL
import pl.sudneu.purple.presentation.PurpleEnvironment.VEC_DATABASE_USERNAME
import java.util.Properties

class ConfigurationShould {

  @Test
  fun `be converted to data source config`() {
    val datasourceConfig: HikariConfig = testEnvironment.toHikariConfig()
    with(datasourceConfig) {
      driverClassName shouldBe testEnvironment[VEC_DATABASE_DRIVER]
      jdbcUrl shouldBe testEnvironment[VEC_DATABASE_URL].toString()
      username shouldBe testEnvironment[VEC_DATABASE_USERNAME]
      testEnvironment[VEC_DATABASE_PASSWORD].use { pwd -> password shouldBe pwd }
    }
  }

  @Test
  fun `be converted to kafka properties`() {
    val properties: Properties = testEnvironment.toProperties()

    with(properties) {
      
    }
  }
}
