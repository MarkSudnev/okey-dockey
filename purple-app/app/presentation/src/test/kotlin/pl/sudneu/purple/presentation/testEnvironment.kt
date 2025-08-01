package pl.sudneu.purple.presentation

import org.http4k.config.Environment
import org.http4k.config.Secret
import org.http4k.connect.amazon.AWS_ACCESS_KEY_ID
import org.http4k.connect.amazon.AWS_REGION
import org.http4k.connect.amazon.AWS_SECRET_ACCESS_KEY
import org.http4k.connect.amazon.core.model.AccessKeyId
import org.http4k.connect.amazon.core.model.Region
import org.http4k.connect.amazon.core.model.SecretAccessKey
import org.http4k.core.Uri
import pl.sudneu.purple.presentation.PurpleEnvironment.AWS_BUCKET_NAME
import pl.sudneu.purple.presentation.PurpleEnvironment.KAFKA_TOPIC
import pl.sudneu.purple.presentation.PurpleEnvironment.VEC_DATABASE_DRIVER
import pl.sudneu.purple.presentation.PurpleEnvironment.VEC_DATABASE_PASSWORD
import pl.sudneu.purple.presentation.PurpleEnvironment.VEC_DATABASE_URL
import pl.sudneu.purple.presentation.PurpleEnvironment.VEC_DATABASE_USERNAME

val testEnvironment = Environment.defaults(
  AWS_REGION of Region.of("eu-central-1"),
  AWS_ACCESS_KEY_ID of AccessKeyId.of("abc-123"),
  AWS_SECRET_ACCESS_KEY of SecretAccessKey.of("abc-123"),
  VEC_DATABASE_DRIVER of "org.h2.Driver",
  VEC_DATABASE_URL of Uri.of("jdbc:h2:mem:purple;mode=PostgreSQL"),
  VEC_DATABASE_USERNAME of "root",
  VEC_DATABASE_PASSWORD of Secret("root"),
  KAFKA_TOPIC of "metadata-topic",
  AWS_BUCKET_NAME of "test-bucket-name"
)
