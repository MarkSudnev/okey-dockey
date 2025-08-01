package pl.sudneu.purple.presentation

import org.http4k.config.EnvironmentKey
import org.http4k.lens.nonBlankString
import org.http4k.lens.of
import org.http4k.lens.secret
import org.http4k.lens.uri

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
