package com.keeganosala.lol.core
package db.postgres.mapper

import scala.concurrent.Future

import com.github.mauricio.async.db.RowData
import com.github.mauricio.async.db.util.ExecutorServiceUtils.CachedExecutionContext

import com.keeganosala._

import lol.core.db.postgres.PostgresDb

import lol.core.db.postgres.service.PostgresDbService.UserInstance

private[postgres] object UserInstanceMapper extends PostgresDb {

  private val FetchUserInstanceSql = "SELECT id, name, email, password FROM users WHERE email = ? AND password = crypt(?, password)"

  def getUserInstance(
    email: String,
    password: String
  ) : Future[Option[UserInstance]] = {
    connection.sendPreparedStatement(
      FetchUserInstanceSql,
      Array[Any](email, password)
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

  private def rowToModel(row: RowData) = UserInstance (
    id       = row("id").asInstanceOf[Int],
    name     = row("name").asInstanceOf[String],
    email    = row("email").asInstanceOf[String],
    password = row("password").asInstanceOf[String]
  )
}
