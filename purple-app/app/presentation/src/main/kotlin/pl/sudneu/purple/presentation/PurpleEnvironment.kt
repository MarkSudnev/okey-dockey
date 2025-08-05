package pl.sudneu.purple.presentation

import com.zaxxer.hikari.HikariConfig
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.http4k.config.Environment
import org.http4k.config.EnvironmentKey
import org.http4k.lens.csv
import org.http4k.lens.nonBlankString
import org.http4k.lens.of
import org.http4k.lens.secret
import org.http4k.lens.uri
import pl.sudneu.purple.presentation.PurpleEnvironment.KAFKA_BOOTSTRAP_SERVERS
import pl.sudneu.purple.presentation.PurpleEnvironment.KAFKA_GROUP_ID
import pl.sudneu.purple.presentation.PurpleEnvironment.VEC_DATABASE_DRIVER
import pl.sudneu.purple.presentation.PurpleEnvironment.VEC_DATABASE_PASSWORD
import pl.sudneu.purple.presentation.PurpleEnvironment.VEC_DATABASE_URL
import pl.sudneu.purple.presentation.PurpleEnvironment.VEC_DATABASE_USERNAME
import java.util.*

object PurpleEnvironment {
  val VEC_DATABASE_DRIVER by EnvironmentKey.nonBlankString().of().required()
  val VEC_DATABASE_URL by EnvironmentKey.uri().of().required()
  val VEC_DATABASE_NAME by EnvironmentKey.nonBlankString().of().required()
  val VEC_DATABASE_USERNAME by EnvironmentKey.nonBlankString().of().required()
  val VEC_DATABASE_PASSWORD by EnvironmentKey.secret().of().required()
  val KAFKA_BOOTSTRAP_SERVERS by EnvironmentKey.csv(",").of().required()
  val KAFKA_GROUP_ID by EnvironmentKey.nonBlankString().of().optional()
  val KAFKA_TOPIC by EnvironmentKey.nonBlankString().of().required()
  val AWS_URL_ENDPOINT by EnvironmentKey.uri().of().required()
  val AWS_BUCKET_NAME by EnvironmentKey.nonBlankString().of().required()
  val OPEN_AI_URL_ENDPOINT by EnvironmentKey.uri().of().required()
}

fun Environment.toHikariConfig(): HikariConfig =
  HikariConfig().also { config ->
    config.driverClassName = this[VEC_DATABASE_DRIVER]
    config.jdbcUrl = this[VEC_DATABASE_URL].toString()
    config.username = this[VEC_DATABASE_USERNAME]
    config.maximumPoolSize = 10
    config.isReadOnly = false
    this[VEC_DATABASE_PASSWORD].use { pwd -> config.password = pwd }
  }

fun Environment.toProperties(): Properties =
  Properties().also { props ->
    this[KAFKA_GROUP_ID]?.let { props[ConsumerConfig.GROUP_ID_CONFIG] = it }
    props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = this[KAFKA_BOOTSTRAP_SERVERS]
  }
