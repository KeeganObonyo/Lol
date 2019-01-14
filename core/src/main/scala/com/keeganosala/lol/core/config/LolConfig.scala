package com.keeganosala.lol.core
package config

import collection.JavaConversions._
import com.typesafe.config.ConfigFactory

import com.keeganosala._

import lol.core.util.LolUtil

object LolConfig extends LolConfig

private[config] trait LolConfig {

  val config = ConfigFactory.load
  config.checkValid(ConfigFactory.defaultReference)

  // Actor-Timeout
  val queryTimeout  = LolUtil.parseFiniteDuration(config.getString("lol.actor-timeout.postgresql")).get
  val brokerTimeout = LolUtil.parseFiniteDuration(config.getString("lol.actor-timeout.broker")).get
  
  // Broker
  val brokerUrlFirst = config.getString("lol.alphavantage.request-url-firstpart")

  val brokerUrlLast  = config.getString("lol.alphavantage.request-url-lastpart")

  
  //http
  val httpRequestTimeout = LolUtil.parseFiniteDuration(config.getString("lol.http.request-timeout")).get

  // API
  val apiInterface = config.getString("lol.interface.web.host")
  val apiPort      = config.getInt("lol.interface.web.port")

  // postgresql
  val postgresqlDbHost  = config.getString("lol.db.postgresql.host")
  val postgresqlDbPort  = config.getInt("lol.db.postgresql.port")
  val postgresqlDbUser  = config.getString("lol.db.postgresql.user")
  val postgresqlDbPass  = config.getString("lol.db.postgresql.pass")
  val postgresqlDbName  = config.getString("lol.db.postgresql.name")

  val postgresqlDbPoolMaxObjects   = config.getInt("lol.db.sql.pool.max-objects")
  val postgresqlDbPoolMaxIdle      = config.getInt("lol.db.sql.pool.max-idle")
  val postgresqlDbPoolMaxQueueSize = config.getInt("lol.db.sql.pool.max-queue-size")

}
