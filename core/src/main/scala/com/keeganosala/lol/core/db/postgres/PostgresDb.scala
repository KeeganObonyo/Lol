package com.keeganosala.lol.core
package db.postgres

import scala.language.postfixOps
import scala.concurrent.Await
import scala.concurrent.duration._

import com.github.mauricio.async.db.postgresql.PostgreSQLConnection
import com.github.mauricio.async.db.Configuration	
import com.github.mauricio.async.db.Connection

import com.keeganosala._

import lol.core.config._

object PostgresDb {

	private val configuration = new Configuration(
	    username = LolConfig.postgresqlDbUser,
	    host     = LolConfig.postgresqlDbHost,
	    port     = LolConfig.postgresqlDbPort,
	    password = Some(LolConfig.postgresqlDbPass),
	    database = Some(LolConfig.postgresqlDbName)
	  )

    private val connection: Connection = new PostgreSQLConnection(configuration)

    Await.result(connection.connect, 1 seconds)

}

private[postgres] trait PostgresDb {
  implicit lazy val connection = PostgresDb.connection
}
