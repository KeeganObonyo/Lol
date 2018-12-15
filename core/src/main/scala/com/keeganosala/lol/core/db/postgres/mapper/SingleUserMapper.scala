package com.keeganosala.lol.core
package db.postgres.mapper

import scala.concurrent.Future

import com.github.mauricio.async.db.RowData
import com.github.mauricio.async.db.util.ExecutorServiceUtils.CachedExecutionContext

import com.keeganosala._

import lol.core.db.postgres.PostgresDb

import lol.core.db.postgres.service.PostgresDbService.User

private[postgres] object SingleUserMapper extends PostgresDb {

  private val FetchSingleUser = "SELECT id, name, email FROM users WHERE id = ?"

  def getSingleUser(
    id: String
  ) : Future[Option[User]] = {
    connection.sendPreparedStatement(
      FetchSingleUser,
      Array[Any](id)
    ).map { queryResult =>
      queryResult.rows match {
        case Some(rows) => {
          if (rows.length > 0) Some(rowToModel(rows.apply(0)))
          else None
        }
        case None => None
      }
    }
  }

  private def rowToModel(row: RowData) = User (
    id       = row("id").asInstanceOf[Int],
    name     = row("name").asInstanceOf[String],
    email    = row("email").asInstanceOf[String]
  )

}
