package pl.sudneu.purple.presentation

import org.http4k.config.Environment
import org.http4k.connect.amazon.AWS_ACCESS_KEY_ID
import org.http4k.connect.amazon.AWS_REGION
import org.http4k.connect.amazon.AWS_SECRET_ACCESS_KEY
import org.http4k.connect.amazon.core.model.AccessKeyId
import org.http4k.connect.amazon.core.model.Region
import org.http4k.connect.amazon.core.model.SecretAccessKey

val testEnvironment = Environment.defaults(
  AWS_REGION of Region.of("eu-central-1"),
  AWS_ACCESS_KEY_ID of AccessKeyId.of("abc-123"),
  AWS_SECRET_ACCESS_KEY of SecretAccessKey.of("abc-123"),
)
