package com.keeganosala.lol.core
package db.postgres.mapper

import scala.concurrent.Future

import com.github.mauricio.async.db.RowData
import com.github.mauricio.async.db.util.ExecutorServiceUtils.CachedExecutionContext

import com.keeganosala._

import lol.core.db.postgres.PostgresDb

import lol.core.db.postgres.service.PostgresDbService.UserDbEntry

private[postgres] object AddUserMapper extends PostgresDb {

  private val AddUserSql = "insert into users (name, email, password) values (?,?,crypt(?, gen_salt('bf', 8)))"

  def addUser(
  	name:String,
  	email:String,
  	password:String
  	) = connection.sendPreparedStatement(AddUserSql, Array[Any](email, email, password))
}
