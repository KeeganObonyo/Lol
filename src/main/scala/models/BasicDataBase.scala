package models

import com.github.mauricio.async.db.postgresql.PostgreSQLConnection
import com.github.mauricio.async.db.postgresql.util.URLParser
import com.github.mauricio.async.db.Connection
import scala.concurrent.Await
import scala.concurrent.duration._



object BasicDataBase {

    val configuration = URLParser.parse("jdbc:postgresql://localhost:5432/lol_database?user=postgres&password=postgres")

    val connection: Connection = new PostgreSQLConnection(configuration)

    Await.result(connection.connect, 1 seconds)

}
