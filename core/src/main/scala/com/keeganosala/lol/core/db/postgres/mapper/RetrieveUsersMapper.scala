package com.keeganosala.lol.core
package db.postgres.mapper

import scala.concurrent.Future

import com.github.mauricio.async.db.RowData
import com.github.mauricio.async.db.util.ExecutorServiceUtils.CachedExecutionContext

import com.keeganosala._

import lol.core.db.postgres.PostgresDb

import lol.core.db.postgres.service.PostgresDbService.User

private[postgres] object RetrieveUsersMapper extends PostgresDb {

  private val FetchUsersSql = "SELECT id, name, email  FROM users"

  def fetchAvailableUsers: Future[List[User]] = {
    pool.sendPreparedStatement(FetchUsersSql).map { queryResult =>
      queryResult.rows match {
        case Some(rows) => rows.toList map (x => rowToModel(x))
        case None       => Nil
      }
    }
  }

  private def rowToModel(row: RowData): User = {
      new User(
        id        = row("id").asInstanceOf[Int],
        name      = row("name").asInstanceOf[String],
        email     = row("email").asInstanceOf[String]
      )
    }

}
