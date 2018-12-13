package com.keeganosala.lol.core
package db.postgres.mapper

import scala.concurrent.Future

import com.github.mauricio.async.db.RowData
import com.github.mauricio.async.db.util.ExecutorServiceUtils.CachedExecutionContext

import com.keeganosala._

import lol.core.db.postgres.PostgresDb

import lol.core.query.QueryService.User


private[postgres] object AddUser extends PostgresDb {

  private val DeleteUserSql = "delete from users where id = ?"

  def deleteUser(name:String) = {
    connection.sendPreparedStatement(DeleteUserSql, Array[Any](name))
    }
}
