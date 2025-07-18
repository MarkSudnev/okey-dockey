package pl.sudneu.purple

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import pl.sudneu.purple.presentation.PurpleEnvironment.VEC_DATABASE_DRIVER
import pl.sudneu.purple.presentation.PurpleEnvironment.VEC_DATABASE_PASSWORD
import pl.sudneu.purple.presentation.PurpleEnvironment.VEC_DATABASE_URL
import pl.sudneu.purple.presentation.PurpleEnvironment.VEC_DATABASE_USERNAME
import javax.sql.DataSource

object TestDatabaseConnection {

  val dataSource: DataSource by lazy {

    HikariConfig().also {
      it.driverClassName = environment[VEC_DATABASE_DRIVER]
      it.jdbcUrl = environment[VEC_DATABASE_URL].toString()
      it.username = environment[VEC_DATABASE_USERNAME]
      it.password = environment[VEC_DATABASE_PASSWORD].toString()
      it.maximumPoolSize = 6
      it.isReadOnly = false
      it.transactionIsolation = "TRANSACTION_SERIALIZABLE"
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
