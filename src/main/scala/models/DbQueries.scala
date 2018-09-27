package models

import com.github.mauricio.async.db.util.ExecutorServiceUtils.CachedExecutionContext
import com.github.mauricio.async.db.{RowData, QueryResult}
import scala.concurrent.Future
import server.{User,Users}


trait DbQueries {

  import BasicDataBase._

	def getUsers()= {

    connection.sendQuery("SELECT * FROM users").map { queryResult => 
      queryResult.rows match {
        case Some(rows) => rowToModelList(rows.toList)
        case None => Nil
      }
    }
  }

  def addUser(args:Array[Any]){
    connection.sendPreparedStatement("insert into users (name, age, countryofresidence) values (?,?,?)",args)
  }

  def deleteUser(args: String){

    val future = connection.sendPreparedStatement("delete from users where id = ? returning id", Array(args))
  }

  def getUser(args:String)= {

    connection.sendPreparedStatement("SELECT id, name, age, countryofresidence FROM users WHERE id = ?", Array(args)).map { 
      queryResult => 
      queryResult.rows match {
        case Some(row) => {
          val listy = row.toList map (x => rowToModel(x))
          try{
            listy(0)
          } catch {
            case _:Throwable => User(id = 0, name = "", age = 0, countryOfResidence = "")
          }
        }
        case None => User(id = 0, name = "", age = 0, countryOfResidence = "")
      }
    }
  }

  private def rowToModel(row: RowData): User = {
      new User(
        id        = row(0).asInstanceOf[Int],
        name       = row(1).asInstanceOf[String],
        age        = row(2).asInstanceOf[Int],
        countryOfResidence = row(3).asInstanceOf[String]
      )
    }
  private def rowToModelList(row: List[RowData]): Users = {
      new Users(
        users     = row.toList map (x => rowToModel(x))
      )
  }
}