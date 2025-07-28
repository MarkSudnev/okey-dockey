package pl.sudneu.purple.infrastructure.postgresql

import com.zaxxer.hikari.HikariDataSource
import dev.forkhandles.result4k.asSuccess
import dev.forkhandles.result4k.kotest.shouldBeSuccess
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import pl.sudneu.purple.domain.EmbeddedDocument
import pl.sudneu.purple.domain.EmbeddedDocumentChunk
import pl.sudneu.purple.domain.StoreDocument
import java.util.UUID
import javax.sql.DataSource

class PostgresqlStoreDocumentShould {

  @BeforeEach
  fun setup() {
    DatabaseUtils.prepare()
  }

  @AfterEach
  fun teardown() {
    DatabaseUtils.clean()
  }

  @Test
  fun `store document in database`() {
    val embeddedDocument =
      EmbeddedDocument(listOf(EmbeddedDocumentChunk("Hello", listOf(0.278634623, 0.9384534))))
    val dataSource = TestDatabaseConnection.dataSource
    val storeDocument = PostgresqlStoreDocument(TestDatabaseConnection.dataSource)
    storeDocument(embeddedDocument).shouldBeSuccess()

    dataSource.connection.use { connection ->
      val result = connection
        .prepareStatement("SELECT * FROM documents")
        .executeQuery()
      result.last()
      result.row shouldBe 1
    }
  }
}

fun PostgresqlStoreDocument(dataSource: DataSource): StoreDocument {
  return StoreDocument { embeddedDocument ->
    embeddedDocument.chunks.forEach { chunk ->
      val id = UUID.randomUUID().toString()
      val embeddings = "[${chunk.embeddings.joinToString(",")}]"
      dataSource.connection.prepareStatement("INSERT INTO documents (id, content, embedding) VALUES (?, ?, ?)")
        .use { stmt ->
          stmt.setString(1, id)
          stmt.setString(2, chunk.content.value)
          stmt.setString(3, embeddings)
          stmt.executeUpdate()
        }
    }
    Unit.asSuccess()
  }
}


