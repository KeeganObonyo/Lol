package com.keeganosala.lol.core
package db.postgres

import scala.language.postfixOps
import scala.concurrent.Await
import scala.concurrent.duration._

import com.github.mauricio.async.db.Configuration	
import com.github.mauricio.async.db.postgresql.pool.PostgreSQLConnectionFactory
import com.github.mauricio.async.db.pool.{ ConnectionPool, PoolConfiguration }
import com.github.mauricio.async.db.postgresql.PostgreSQLConnection

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

	private val poolConfiguration = new PoolConfiguration(
		maxObjects   = LolConfig.sqlDbPoolMaxObjects,
		maxIdle      = LolConfig.sqlDbPoolMaxIdle,
		maxQueueSize = LolConfig.sqlDbPoolMaxQueueSize
	)

	private val factory = new PostgreSQLConnectionFactory(configuration)

    private val pool = new ConnectionPool(factory, poolConfiguration)
}

private[postgres] trait PostgresDb {
  implicit lazy val pool = PostgresDb.pool
}
