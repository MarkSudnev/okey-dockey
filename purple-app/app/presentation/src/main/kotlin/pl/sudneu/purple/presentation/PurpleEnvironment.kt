package pl.sudneu.purple.presentation

import com.zaxxer.hikari.HikariConfig
import org.http4k.config.Environment
import org.http4k.config.EnvironmentKey
import org.http4k.lens.nonBlankString
import org.http4k.lens.of
import org.http4k.lens.secret
import org.http4k.lens.uri
import pl.sudneu.purple.presentation.PurpleEnvironment.VEC_DATABASE_DRIVER
import pl.sudneu.purple.presentation.PurpleEnvironment.VEC_DATABASE_PASSWORD
import pl.sudneu.purple.presentation.PurpleEnvironment.VEC_DATABASE_URL
import pl.sudneu.purple.presentation.PurpleEnvironment.VEC_DATABASE_USERNAME

object PurpleEnvironment {
  val VEC_DATABASE_DRIVER by EnvironmentKey.nonBlankString().of().required()
  val VEC_DATABASE_URL by EnvironmentKey.uri().of().required()
  val VEC_DATABASE_NAME by EnvironmentKey.nonBlankString().of().required()
  val VEC_DATABASE_USERNAME by EnvironmentKey.nonBlankString().of().required()
  val VEC_DATABASE_PASSWORD by EnvironmentKey.secret().of().required()
  val KAFKA_SERVER by EnvironmentKey.uri().of().required()
  val KAFKA_TOPIC by EnvironmentKey.nonBlankString().of().required()
  val AWS_BUCKET_NAME by EnvironmentKey.nonBlankString().of().required()
}

fun Environment.toHikariConfig(): HikariConfig =
  HikariConfig().also { config ->
    config.driverClassName = this[VEC_DATABASE_DRIVER]
    config.jdbcUrl = this[VEC_DATABASE_URL].toString()
    config.username = this[VEC_DATABASE_USERNAME]
    config.maximumPoolSize = 10
    config.isReadOnly = false
    this[VEC_DATABASE_PASSWORD].use { pwd -> config.password = pwd}
  }
