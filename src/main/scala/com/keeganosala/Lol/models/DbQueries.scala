package com.keeganosala.Lol
package models

import com.github.mauricio.async.db.util.ExecutorServiceUtils.CachedExecutionContext
import com.github.mauricio.async.db.{RowData, QueryResult}
import scala.concurrent.Future
import users.{User,Users,UserInstance}
import auth.LoginRequest


trait DbQueries {

  import BasicDataBase._

	def getUsers()= {

    connection.sendQuery("SELECT id, name, email  FROM users").map { queryResult => 
      queryResult.rows match {
        case Some(rows) => rowToModelList(rows.toList)
        case None => Nil
      }
    }
  }

  def addUser(args:Array[Any]){
    connection.sendPreparedStatement("insert into users (name, email, password) values (?,?,crypt(?, gen_salt('bf', 8)))",args)
  }

  def deleteUser(args: String){

    val future = connection.sendPreparedStatement("delete from users where id = ? returning id", Array(args))
  }

  def getUser(args:String)= {

    connection.sendPreparedStatement("SELECT id, name, email FROM users WHERE id = ?", Array(args)).map { 
      queryResult => 
      queryResult.rows match {
        case Some(row) => {
          val listy = row.toList map (x => rowToModel(x))
          try{
            listy(0)
          } catch {
            case _:Throwable => User(id = 0, name = "", email = "")
          }
        }
        case None => User(id = 0, name = "", email = "")
      }
    }
  }

  def getUserInstance(args:Array[Any]) = {
    val queryResult = connection.sendPreparedStatement("SELECT id, name, email, password FROM users WHERE email = ? AND password = crypt(?, password)",args)
    queryResult.map { result => 
      	result.rows match {
      		case Some(row) => val listy = row.toList map (x => rowToModelUserInstance(x));Left(listy(0))
      		case None => UserInstance(id = 0, name = "", email = "" , password = "")
      	}
      }
    }

  private def rowToModel(row: RowData): User = {
      new User(
        id        = row(0).asInstanceOf[Int],
        name       = row(1).asInstanceOf[String],
        email        = row(2).asInstanceOf[String]
      )
    }
  private def rowToModelUserInstance(row: RowData): UserInstance = {
      new UserInstance(
        id        = row(0).asInstanceOf[Int],
        name       = row(1).asInstanceOf[String],
        email        = row(2).asInstanceOf[String],
        password = row(3).asInstanceOf[String]
      )
    }
  private def rowToModelList(row: List[RowData]): Users = {
      new Users(
        users     = row.toList map (x => rowToModel(x))
      )
  }
}

