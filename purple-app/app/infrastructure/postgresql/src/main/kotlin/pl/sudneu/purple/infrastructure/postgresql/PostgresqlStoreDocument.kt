package pl.sudneu.purple.infrastructure.postgresql

import dev.forkhandles.result4k.mapFailure
import dev.forkhandles.result4k.resultFrom
import pl.sudneu.purple.domain.EmbeddedDocumentChunk
import pl.sudneu.purple.domain.PurpleError.StoreDocumentError
import pl.sudneu.purple.domain.StoreDocument
import java.sql.Connection
import java.sql.Types.ARRAY
import javax.sql.DataSource

fun PostgresqlStoreDocument(dataSource: DataSource): StoreDocument =
  StoreDocument { embeddedDocument ->
    resultFrom {
      embeddedDocument.chunks.forEach { chunk ->
        dataSource.connection.use { conn -> conn.executeInsert(chunk) }
      }
    }.mapFailure { exception ->
      StoreDocumentError("${exception::class.simpleName}: ${exception.message}")
    }
  }

private fun Connection.executeInsert(chunk: EmbeddedDocumentChunk) {
  prepareStatement("INSERT INTO documents (content, embedding) VALUES (?, ?)")
    .use { stmt ->
      stmt.setString(1, chunk.content.value)
      stmt.setObject(2, chunk.embeddings.toDoubleArray(), ARRAY)
      stmt.executeUpdate()
    }
}

fun StoreDocument.Companion.withPostgresql(dataSource: DataSource) = PostgresqlStoreDocument(dataSource)
