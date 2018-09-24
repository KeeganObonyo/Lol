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

  def getUsers():Any= {

    Await.result(connection.connect, 5 seconds)

    val future: Future[QueryResult] = connection.sendQuery("SELECT * FROM users")

    try {
        val users : Future[Users] = future.map(queryResult => queryResult.rows match {

        case Some(resultSet) => {
            val row : RowData = resultSet.head
            row(0)
            }
        case None => -1
        }
        ).mapTo[Users]
        users
    } catch {
      case ex: FileNotFoundException =>{
          server.User
      }
      case ex: IOException =>{
          server.Users
      }
  }
    connection.disconnect
  }

  def addUser(user:server.User){

    val future = connection.sendPreparedStatement("insert to users (name, age, countryofresidence) values ($1, $2, $3) returning id")

    connection.disconnect

  }

  def deleteUser(args: String){

    val future = connection.sendPreparedStatement("delete from users where users.name = ?", Array(args))

    connection.disconnect

  }

  def getUser(args:String) : Any = {

    Await.result(connection.connect, 5 seconds)

    val future = connection.sendPreparedStatement("SELECT * FROM users WHERE users.name = ?", Array(args))

    try{
        val user : Future[User] = future.map(queryResult => queryResult.rows match {

        case Some(resultSet) => {
            val row : RowData = resultSet.head
            row(0)
            }
        case None => -1
        }
        ).mapTo[User]
        user
    } catch {

      case ex: FileNotFoundException =>{
          server.User
      }
      case ex: IOException =>{
          server.User
      }
    }

  }
    connection.disconnect

}
