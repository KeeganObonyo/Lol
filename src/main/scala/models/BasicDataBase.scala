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
import server.User
import java.io.IOException
import java.io.FileNotFoundException



object BasicDataBase {

    val configuration = URLParser.parse("jdbc:postgresql://localhost:5432/lol_database?user=postgres&password=postgres")

    val connection: Connection = new PostgreSQLConnection(configuration)

    Await.result(connection.connect, 1 seconds)

    def apply(): BasicDataBase = {
        new BasicDataBase()
 }


}

class BasicDataBase {

  import BasicDataBase._

  def getUsers(): Future[List[User]]= {

    connection.sendQuery("SELECT * FROM users").map { queryResult => 
      queryResult.rows match {
        case Some(rows) => rows.toList map (x => rowToModel(x))
        case None => List()
      }
    }
  }

  def addUser(args:Array[Any]){
    connection.sendPreparedStatement("insert into users (name, age, countryofresidence) values (?,?,?)",args)
  }

  def deleteUser(args: String){

    val future = connection.sendPreparedStatement("delete from users where name = ? returning name", Array(args))
  }

  def getUser(args:String)= {

    connection.sendPreparedStatement("SELECT id, name, age, countryofresidence FROM users WHERE name = ?", Array(args)).map { 
      queryResult => 
      queryResult.rows match {
        case Some(row) => {
          val listy = row.toList map (x => rowToModel(x))
          try{
            listy(0)
          } catch {
            case _:Throwable => User(name = "", age = 0, countryOfResidence = "")
          }
        }
        case None => User(name = "", age = 0, countryOfResidence = "")
      }
    }
  }

  private def rowToModel(row: RowData): User = {
      new User(
        name       = row(1).asInstanceOf[String],
        age        = row(2).asInstanceOf[Int],
        countryOfResidence = row(3).asInstanceOf[String]
      )
    }
}