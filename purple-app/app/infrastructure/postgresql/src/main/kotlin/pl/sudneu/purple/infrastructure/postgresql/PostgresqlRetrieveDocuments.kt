package pl.sudneu.purple.infrastructure.postgresql

import com.pgvector.PGvector
import dev.forkhandles.result4k.asSuccess
import dev.forkhandles.result4k.mapFailure
import dev.forkhandles.result4k.resultFrom
import pl.sudneu.purple.domain.Document
import pl.sudneu.purple.domain.PurpleError
import pl.sudneu.purple.domain.retrieve.RetrieveDocuments
import pl.sudneu.purple.domain.toPurpleMessage
import javax.sql.DataSource

fun PostgresqlRetrieveDocuments(datasource: DataSource): RetrieveDocuments {
  return RetrieveDocuments { query ->
    val output = mutableListOf<String>()
    resultFrom {
      datasource.connection.use { conn ->
        PGvector.registerTypes(conn)
        conn.prepareStatement("SELECT * FROM documents ORDER BY embedding <-> ? LIMIT ?")
          .use { stmt ->
            stmt.setObject(1, PGvector(query.embedding))
            stmt.setInt(2, query.resultsCount.value)
            val result = stmt.executeQuery()
            while (result.next()) {
              output.add(result.getString(2))
            }
          }
      }
    output.map { Document(it) }
    }.mapFailure { PurpleError.RetrieveDocumentsError(it.toPurpleMessage()) }
  }
}

fun RetrieveDocuments.Companion.withPostgresql(datasource: DataSource) = PostgresqlRetrieveDocuments(datasource)
