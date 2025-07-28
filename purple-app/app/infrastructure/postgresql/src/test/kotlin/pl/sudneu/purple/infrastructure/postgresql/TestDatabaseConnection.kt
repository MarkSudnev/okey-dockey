package pl.sudneu.purple.infrastructure.postgresql

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource

import javax.sql.DataSource

object TestDatabaseConnection {

  val dataSource: DataSource by lazy {

    HikariConfig().also {
      it.driverClassName = "org.h2.Driver"
      it.jdbcUrl = "jdbc:h2:mem:purple;mode=PostgreSQL"
      it.username = "root"
      it.password = "root"
      it.maximumPoolSize = 6
      it.isReadOnly = false
    }.let { HikariDataSource(it) }
  }
}

class DatabaseUtils {
  companion object
}

fun DatabaseUtils.Companion.prepare() {
  TestDatabaseConnection
    .dataSource
    .connection
    .createStatement()
    .execute("CREATE TABLE IF NOT EXISTS documents (id UUID PRIMARY KEY, content VARCHAR NOT NULL, embedding VARCHAR NOT NULL);")
}

fun DatabaseUtils.Companion.clean() {
  TestDatabaseConnection
    .dataSource
    .connection
    .createStatement()
    .execute("DELETE FROM documents")
}
