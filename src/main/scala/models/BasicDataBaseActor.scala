package models

import akka.actor.{ Actor, ActorLogging, Props }
import com.github.mauricio.async.db.postgresql.PostgreSQLConnection
import com.github.mauricio.async.db.postgresql.util.URLParser
import com.github.mauricio.async.db.util.ExecutorServiceUtils.CachedExecutionContext
import com.github.mauricio.async.db.{RowData, QueryResult, Connection}
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object BasicDataBaseActor {

    val configuration = URLParser.parse("jdbc:postgresql://localhost:5432/lol_database?user=postgres&password=postgres")

    final case object Connected
    
  def props: Props = Props[BasicDataBaseActor]

}

// Actor for handling database connections to allow for concurrent connections to the database  

class BasicDataBaseActor extends Actor with ActorLogging {

    import BasicDataBaseActor._

    def receive : Receive = {

        case Connected =>

        val  connection = new PostgreSQLConnection(configuration) 

    } 
  
}

