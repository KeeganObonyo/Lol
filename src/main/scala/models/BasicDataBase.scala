package models

import com.github.mauricio.async.db.postgresql.PostgreSQLConnection
import com.github.mauricio.async.db.postgresql.util.URLParser
import com.github.mauricio.async.db.util.ExecutorServiceUtils.CachedExecutionContext
import com.github.mauricio.async.db.{RowData, QueryResult, Connection}
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}


object BasicDatabase {

  def main(args: Array[String]) {

    val configuration = URLParser.parse("jdbc:postgresql://localhost:5432/lol_database?user=postgres&password=postgres")
    val connection: Connection = new PostgreSQLConnection(configuration)

    Await.result(connection.connect, 5 seconds)

  }

}

//TODO create functions for managing the database calls

    // val future: Future[QueryResult] = connection.sendQuery("SELECT 0")

    // val mapResult: Future[Any] = future.map(queryResult => queryResult.rows match {
    //   case Some(resultSet) => {
    //     val row : RowData = resultSet.head
    //     row(0)
    //   }
    //   case None => -1
    // }
    // )

    // val result = Await.result( mapResult, 5 seconds )

    // println(result)

    // connection.disconnect