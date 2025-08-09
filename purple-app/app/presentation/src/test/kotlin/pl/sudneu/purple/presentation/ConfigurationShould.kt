package pl.sudneu.purple.presentation

import com.zaxxer.hikari.HikariConfig
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.http4k.connect.amazon.AWS_ACCESS_KEY_ID
import org.http4k.connect.amazon.AWS_REGION
import org.http4k.connect.amazon.AWS_SECRET_ACCESS_KEY
import org.junit.jupiter.api.Test
import pl.sudneu.purple.infrastructure.aws.AwsParameters
import pl.sudneu.purple.presentation.PurpleEnvironment.AWS_BUCKET_NAME
import pl.sudneu.purple.presentation.PurpleEnvironment.AWS_URL_ENDPOINT
import pl.sudneu.purple.presentation.PurpleEnvironment.KAFKA_BOOTSTRAP_SERVERS
import pl.sudneu.purple.presentation.PurpleEnvironment.KAFKA_GROUP_ID
import pl.sudneu.purple.presentation.PurpleEnvironment.VEC_DATABASE_DRIVER
import pl.sudneu.purple.presentation.PurpleEnvironment.VEC_DATABASE_PASSWORD
import pl.sudneu.purple.presentation.PurpleEnvironment.VEC_DATABASE_URL
import pl.sudneu.purple.presentation.PurpleEnvironment.VEC_DATABASE_USERNAME
import java.util.*

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
      properties[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] shouldBe testEnvironment[KAFKA_BOOTSTRAP_SERVERS]
      properties[ConsumerConfig.GROUP_ID_CONFIG] shouldBe testEnvironment[KAFKA_GROUP_ID]
    }
  }

  @Test
  fun `be converted to aws parameters`() {
    val params: AwsParameters = testEnvironment.toAwsParameters()

    val credentials = params.credentialsProvider()
    credentials.accessKey shouldBe testEnvironment[AWS_ACCESS_KEY_ID].value
    credentials.secretKey shouldBe testEnvironment[AWS_SECRET_ACCESS_KEY].value

    with(params) {
      bucketName shouldBe testEnvironment[AWS_BUCKET_NAME]
      region shouldBe testEnvironment[AWS_REGION]
      client.shouldNotBeNull()
      awsUrlEndpoint shouldBe testEnvironment[AWS_URL_ENDPOINT]
    }
  }
}
