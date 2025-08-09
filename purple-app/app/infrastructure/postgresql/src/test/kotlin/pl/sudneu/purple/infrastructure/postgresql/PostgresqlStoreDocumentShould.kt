package pl.sudneu.purple.infrastructure.postgresql

import com.pgvector.PGvector
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.forkhandles.result4k.kotest.shouldBeFailure
import dev.forkhandles.result4k.kotest.shouldBeSuccess
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable
import org.testcontainers.containers.PostgreSQLContainer
import pl.sudneu.purple.domain.store.EmbeddedDocument
import pl.sudneu.purple.domain.store.EmbeddedDocumentChunk
import pl.sudneu.purple.domain.PurpleError.StoreDocumentError
import javax.sql.DataSource
import kotlin.random.Random

private const val vectorSize = 1152

@DisabledIfEnvironmentVariable(named = "SKIP_TEST_CONTAINERS", matches = "true")
class PostgresqlStoreDocumentShould : PostgresqlRunner() {

  @BeforeEach
  fun setup() {
    datasource.connection.use { connection ->
      connection.createStatement().execute("CREATE EXTENSION IF NOT EXISTS vector;")
      connection.createStatement().execute(
        """CREATE TABLE IF NOT EXISTS documents (
        id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
        content VARCHAR NOT NULL,
        embedding vector($vectorSize) NOT NULL);"""
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
    val embeddedDocument = EmbeddedDocument(
      listOf(EmbeddedDocumentChunk("Hello", randomEmbeddings))
    )
    val storeDocument = PostgresqlStoreDocument(datasource)
    storeDocument(embeddedDocument).shouldBeSuccess()

    datasource.connection.use { connection ->
      PGvector.registerTypes(connection)
      val result = connection
        .prepareStatement("SELECT * FROM documents")
        .executeQuery()
      result.next()
      result.getString(2) shouldBe "Hello"
      result.getObject(3, PGvector::class.java).toArray() shouldHaveSize vectorSize
    }
  }

  @Test
  fun `return failure when vector size is invalid`() {
    val embeddedDocument = EmbeddedDocument(
      listOf(EmbeddedDocumentChunk(
        "Hello",
        listOf(0.7634587365, 0.364583)
      ))
    )
    val storeDocument = PostgresqlStoreDocument(datasource)
    storeDocument(embeddedDocument) shouldBeFailure StoreDocumentError(
      "PSQLException: ERROR: expected $vectorSize dimensions, not 2"
    )
  }
}

val randomEmbeddings: List<Double> = (1..vectorSize).map { Random.nextDouble() }
