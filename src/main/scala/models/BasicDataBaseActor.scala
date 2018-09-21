package models

import akka.actor.{ Actor, ActorLogging, Props }
import com.github.mauricio.async.db.postgresql.PostgreSQLConnection
import com.github.mauricio.async.db.postgresql.util.URLParser
import com.github.mauricio.async.db.util.ExecutorServiceUtils.CachedExecutionContext
import com.github.mauricio.async.db.{RowData, QueryResult, Connection}
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import akka.util.Timeout
import scala.concurrent.duration._
import java.util.concurrent.TimeUnit

object BasicDataBaseActor {


    final case class Write(statement : String)

    final case class Query(statement: String)
    

}


// Actor for handling database connections to allow for concurrent connections to the database  

class BasicDataBaseActor extends Actor with ActorLogging {

    import BasicDataBaseActor._

    val configuration = URLParser.parse("jdbc:postgresql://localhost:5432/lol_database?user=postgres&password=postgres")

    val  connection = new PostgreSQLConnection(configuration) 

    def receive : Receive = {
//write to the database
        case Write(statement) =>

        val future = connection.sendPreparedStatement(statement)

        connection.disconnect

//query the database
        case Query(statement) =>

        val future: Future[QueryResult] = connection.sendQuery(statement)

        val mapResult: Future[Any] = future.map(queryResult => queryResult.rows match {

        case Some(resultSet) => {

        val row : RowData = resultSet.head

        row(0)
      }
      case None => -1
    }
    )

    val result = Await.result( mapResult, 5 seconds )

    connection.disconnect

    } 
  
}
