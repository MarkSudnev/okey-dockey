package pl.sudneu.purple.infrastructure.postgresql

import dev.forkhandles.result4k.kotest.shouldBeFailure
import dev.forkhandles.result4k.kotest.shouldBeSuccess
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.should
import io.kotest.matchers.string.shouldStartWith
import io.kotest.matchers.types.beInstanceOf
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable
import pl.sudneu.purple.domain.PurpleError.RetrieveDocumentsError
import pl.sudneu.purple.domain.retrieve.EmbeddedQuery
import pl.sudneu.purple.shared.toPositiveInteger
import java.sql.Types.ARRAY

private const val vectorSize = 3

@DisabledIfEnvironmentVariable(named = "SKIP_TEST_CONTAINERS", matches = "true")
class PostgresqlRetrieveDocumentsShould : PostgresqlRunner() {

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
  fun `fetch documents by embedding`() {
    datasource.connection.use { conn ->
      testRecords.forEach { record ->
        conn.prepareStatement("INSERT INTO documents (content, embedding) VALUES (?, ?)")
          .use { stmt ->
            stmt.setString(1, record.first)
            stmt.setObject(2, record.second, ARRAY)
            stmt.executeUpdate()
          }
      }
    }
    val retrieveDocuments = PostgresqlRetrieveDocuments(datasource)

    val embeddedQuery = EmbeddedQuery(listOf(0.1, 0.2, 0.3), 2.toPositiveInteger())
    retrieveDocuments(embeddedQuery) shouldBeSuccess { result ->
      result shouldHaveSize 2
      result.map { it.content.value } shouldContainAll listOf(
        "Elementum posuere etiam mauris magna.",
        "Massa viverra nec nostra arcu rutrum aliquet ligula."
      )
    }
  }

  @Test
  fun `return failure when query contains empty embeddings`() {
    val retrieveDocuments = PostgresqlRetrieveDocuments(datasource)

    val embeddedQuery = EmbeddedQuery(emptyList(), 2.toPositiveInteger())
    retrieveDocuments(embeddedQuery) shouldBeFailure { error ->
      error should beInstanceOf<RetrieveDocumentsError>()
      error.message shouldStartWith "PSQLException: ERROR: vector must have at least 1 dimension"
    }
  }
}

private val testRecords = listOf(
  "Elementum posuere etiam mauris magna." to listOf(0.1, 0.2, 0.3).toDoubleArray(),
  "Sagittis tortor adipiscing lobortis velit venenatis mauris." to listOf(0.9, 0.9, 0.9).toDoubleArray(),
  "Massa viverra nec nostra arcu rutrum aliquet ligula." to listOf(0.3, 0.2, 0.1).toDoubleArray(),
  "Mattis dolor odio elit cubilia." to listOf(0.1, 0.001, 0.0009).toDoubleArray(),
)
