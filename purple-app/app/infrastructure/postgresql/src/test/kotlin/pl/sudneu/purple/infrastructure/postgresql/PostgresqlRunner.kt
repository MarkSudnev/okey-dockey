package pl.sudneu.purple.infrastructure.postgresql

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.testcontainers.containers.PostgreSQLContainer
import javax.sql.DataSource

open class PostgresqlRunner {

  protected val datasource: DataSource by lazy {
    HikariConfig().also { config ->
      config.driverClassName = "org.postgresql.Driver"
      config.jdbcUrl = pgVectorContainer.jdbcUrl
      config.username = pgVectorContainer.username
      config.password = pgVectorContainer.password
      config.maximumPoolSize = 6
      config.isReadOnly = false
    }.let { HikariDataSource(it) }
  }

  companion object {

    val pgVectorContainer = PostgreSQLContainer("pgvector/pgvector:pg16")
      .withDatabaseName("dockey")
      .withUsername("root")
      .withPassword("root")

    @BeforeAll
    @JvmStatic
    fun runInfrastructure() {
      pgVectorContainer.start()
    }

    @AfterAll
    @JvmStatic
    fun stopInfrastructure() {
      pgVectorContainer.stop()
    }
  }
}
