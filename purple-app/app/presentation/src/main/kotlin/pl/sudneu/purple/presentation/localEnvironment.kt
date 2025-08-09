package pl.sudneu.purple.presentation

import org.http4k.config.Environment
import org.http4k.config.Secret
import org.http4k.connect.amazon.AWS_ACCESS_KEY_ID
import org.http4k.connect.amazon.AWS_REGION
import org.http4k.connect.amazon.AWS_SECRET_ACCESS_KEY
import org.http4k.connect.amazon.core.model.AccessKeyId
import org.http4k.connect.amazon.core.model.Region
import org.http4k.connect.amazon.core.model.SecretAccessKey
import org.http4k.connect.amazon.s3.model.BucketName
import org.http4k.core.Uri
import pl.sudneu.purple.presentation.PurpleEnvironment.AWS_BUCKET_NAME
import pl.sudneu.purple.presentation.PurpleEnvironment.AWS_URL_ENDPOINT
import pl.sudneu.purple.presentation.PurpleEnvironment.KAFKA_BOOTSTRAP_SERVERS
import pl.sudneu.purple.presentation.PurpleEnvironment.KAFKA_GROUP_ID
import pl.sudneu.purple.presentation.PurpleEnvironment.KAFKA_TOPIC
import pl.sudneu.purple.presentation.PurpleEnvironment.OPEN_AI_URL_ENDPOINT
import pl.sudneu.purple.presentation.PurpleEnvironment.VEC_DATABASE_PASSWORD
import pl.sudneu.purple.presentation.PurpleEnvironment.VEC_DATABASE_URL
import pl.sudneu.purple.presentation.PurpleEnvironment.VEC_DATABASE_USERNAME

internal val localEnvironment = Environment.defaults(
  VEC_DATABASE_USERNAME of "admin",
  VEC_DATABASE_PASSWORD of Secret("admin"),
  VEC_DATABASE_URL of Uri.Companion.of("jdbc:postgresql://localhost:5432/dockey"),
  KAFKA_BOOTSTRAP_SERVERS of listOf("localhost:29092"),
  KAFKA_TOPIC of "dockey",
  KAFKA_GROUP_ID of "abc-123",
  AWS_URL_ENDPOINT of Uri.Companion.of("http://localhost:9000"),
  AWS_REGION of Region.Companion.of("us-east-1"),
  AWS_BUCKET_NAME of BucketName.Companion.of("dockey-bucket"),
  AWS_ACCESS_KEY_ID of AccessKeyId.Companion.of("user"),
  AWS_SECRET_ACCESS_KEY of SecretAccessKey.Companion.of("password"),
  OPEN_AI_URL_ENDPOINT of Uri.Companion.of("http://localhost:8090")
)
