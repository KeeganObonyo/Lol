package com.keeganosala.lol.core
package db.postgres

import org.slf4j.LoggerFactory

import com.github.mauricio.async.db.QueryResult

import com.keeganosala.lol.core

import core.util._


trait LolLog {
  def log = LoggerFactory.getLogger(this.getClass)
}

case class PostgresDbQueryResult(
  rowsAffected: Long,
) extends LolLog

object PostgresDbQueryResult {
  def apply(result: QueryResult) = new PostgresDbQueryResult(
    rowsAffected = result.rowsAffected,
  )
}
