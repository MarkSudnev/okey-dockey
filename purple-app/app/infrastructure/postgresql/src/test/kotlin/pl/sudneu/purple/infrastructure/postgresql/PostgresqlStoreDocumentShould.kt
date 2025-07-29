package pl.sudneu.purple.infrastructure.postgresql

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.forkhandles.result4k.asSuccess
import dev.forkhandles.result4k.kotest.shouldBeSuccess
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.containers.PostgreSQLContainer
import pl.sudneu.purple.domain.EmbeddedDocument
import pl.sudneu.purple.domain.EmbeddedDocumentChunk
import pl.sudneu.purple.domain.StoreDocument
import java.sql.Types
import java.util.UUID
import javax.sql.DataSource
import kotlin.random.Random


class PostgresqlStoreDocumentShould {

  private val datasource: DataSource by lazy {
    HikariConfig().also {
      it.driverClassName = "org.postgresql.Driver"
      it.jdbcUrl = pgVectorContainer.jdbcUrl
      it.username = pgVectorContainer.username
      it.password = pgVectorContainer.password
      it.maximumPoolSize = 6
      it.isReadOnly = false
    }.let { HikariDataSource(it) }
  }

  @BeforeEach
  fun setup() {
    datasource.connection.use { connection ->
      connection.createStatement().execute("CREATE EXTENSION vector;")
      connection.createStatement().execute(
        "CREATE TABLE IF NOT EXISTS documents (id VARCHAR PRIMARY KEY, content VARCHAR NOT NULL, embedding vector(1152) NOT NULL);"
      )
    }
  }

  @AfterEach
  fun teardown() {
    datasource.connection.use { connection ->
      connection.prepareStatement("DELETE FROM documents").executeUpdate()
    }
  }

  @Test
  fun `store document in database`() {
    val embeddedDocument =
      EmbeddedDocument(listOf(EmbeddedDocumentChunk("Hello", embeddings)))
    val storeDocument = PostgresqlStoreDocument(datasource)
    storeDocument(embeddedDocument).shouldBeSuccess()

    datasource.connection.use { connection ->
      val result = connection
        .prepareStatement("SELECT * FROM documents")
        .executeQuery()
      result.next()
      result.getString(2) shouldBe "Hello"
    }
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

val embeddings: List<Double> = (1..1152).map { _ -> Random.nextDouble() }

fun PostgresqlStoreDocument(dataSource: DataSource): StoreDocument {
  return StoreDocument { embeddedDocument ->
    embeddedDocument.chunks.forEach { chunk ->
      val id = UUID.randomUUID().toString()
      dataSource
        .connection.use { conn ->
          conn.prepareStatement("INSERT INTO documents (id, content, embedding) VALUES (?, ?, ?)")
          .use { stmt ->
            stmt.setString(1, id)
            stmt.setString(2, chunk.content.value)
            stmt.setObject(3, chunk.embeddings.toDoubleArray(), Types.ARRAY)
            stmt.executeUpdate()
          }
        }
    }
    Unit.asSuccess()
  }
}


