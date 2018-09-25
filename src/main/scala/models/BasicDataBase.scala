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
import server._
import java.io.IOException
import java.io.FileNotFoundException



object BasicDataBase {

    val configuration = URLParser.parse("jdbc:postgresql://localhost:5432/lol_database?user=postgres&password=postgres")

    val connection: Connection = new PostgreSQLConnection(configuration)

  def getUsers(): Future[List[User]]= {

    // Await.result(connection.connect, 5 seconds)

    connection.sendQuery("SELECT * FROM users").map { queryResult => 
      queryResult.rows match {
        case Some(rows) => connection.disconnect ; rows.toList map (x => rowToModel(x))
        case None => connection.disconnect ; List()
      }
    }
  }

  def addUser(args:Array[Any]){

    // Await.result(connection.connect, 5 seconds)

    connection.sendPreparedStatement("insert into users (name, age, countryofresidence) values (?,?,?)",args)
    // connection.disconnect
  }

  def deleteUser(args: String){

    connection.sendPreparedStatement("delete from users where name = ?", Array(args))
    // connection.disconnect

  }

  def getUser(args:String): Future[List[User]]= {

    // Await.result(connection.connect, 5 seconds)

    connection.sendPreparedStatement("SELECT id, name, age, countryofresidence FROM users WHERE name = ?", Array(args)).map { 
      queryResult => 
      queryResult.rows match {
        case Some(rows) => connection.disconnect ; rows.toList map (x => rowToModel(x))
        case None => connection.disconnect ; List() 
        case _ => connection.disconnect ; List()
      }
    }
  }

  private def rowToModel(row: RowData): User = {
      new User(
        name       = row("name").asInstanceOf[String],
        age        = row("age").asInstanceOf[Int],
        countryOfResidence = row("countryofresidence").asInstanceOf[String]
      )
    }
}
