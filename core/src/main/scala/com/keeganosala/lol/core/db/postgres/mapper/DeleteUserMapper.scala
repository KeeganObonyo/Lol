package com.keeganosala.lol.core
package db.postgres.mapper

import scala.concurrent.Future

import com.github.mauricio.async.db.RowData
import com.github.mauricio.async.db.util.ExecutorServiceUtils.CachedExecutionContext

import com.keeganosala._

import lol.core.db.postgres.PostgresDb

private[postgres] object DeleteUser extends PostgresDb {

  private val DeleteUserSql = "delete from users where id = ?"

  def deleteUser(name:String) = {
    connection.sendPreparedStatement(DeleteUserSql, Array[Any](name))
    }
}
