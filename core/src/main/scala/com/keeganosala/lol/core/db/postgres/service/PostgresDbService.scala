package com.keeganosala.lol.core
package db.postgres.service

import java.net.URL

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.ExecutionContext.Implicits.global

import akka.actor.{ Actor, ActorLogging }
import akka.pattern.pipe
import akka.event.Logging

import com.github.mauricio.async.db.QueryResult

import com.keeganosala._

import lol.core.db.postgres.mapper._

import lol.core.db.postgres.PostgresDbQueryResult

import lol.core.util._

object PostgresDbService {

  case class UserDbEntry(
    name: String,
    email: String,
    password:String
  )

  case class User(
    id:Int,
    name: String,
    email: String
  )

  case class UserDbRetrieve(
    email: String,
    password: String
  )

  case class UserInstance(
    id:Int,
    name: String,
    email: String,
    password:String
  )

  case object UsersFetchDbServiceRequest
}

class PostgresDbService extends Actor
    with ActorLogging {

  val system = context.system

  import PostgresDbService._

  def receive = {
    case UsersFetchDbServiceRequest =>
      val currentSender = sender
      RetrieveUsersMapper.fetchAvailableUsers.mapTo[List[User]] pipeTo currentSender

    case user:UserDbEntry =>
      val currentSender = sender
      log.info("processing " + user)
      AddUserMapper.addUser(
                      name     = user.name,
                      email    = user.email,
                      password = user.password
    ).mapTo[QueryResult] map { x => currentSender ! PostgresDbQueryResult(x)}

    case userinstanceretrieve:UserDbRetrieve=>
      val currentSender = sender
      UserInstanceMapper.getUserInstance(
                      email    = userinstanceretrieve.email,
                      password = userinstanceretrieve.password
    ).mapTo[UserInstance] pipeTo currentSender
  }
}
