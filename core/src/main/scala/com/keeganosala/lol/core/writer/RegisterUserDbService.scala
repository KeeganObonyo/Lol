package com.keeganosala.lol.core
package writer

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{ Failure, Success }

import akka.actor.{ Actor, ActorLogging, Props }
import akka.pattern.ask
import akka.util.Timeout
import akka.event.Logging

import com.keeganosala._

import lol.core.config.LolConfig
import lol.core.db.mysql.service.PostgresDbService
import lol.core.db.postgres.PostgresDbQueryResult


object RegisterUserDbService {

  case class RegisterUser(
    name: String,
    email: String,
    password:String
  )
}

class RegisterUserDbService extends Actor
    with ActorLogging{


  implicit val actorSystem       = context.system

  implicit val timeout           = Timeout(LolConfig.queryTimeout)

  def createPostgresDbService    = context.actorOf(Props[PostgresDbService])

  private val postgresDbService  = createPostgresDbService

  val log                        = Logging(actorSystem, classOf[RegisterUserDbService])

  import RegisterUserDbService._
  import PostgresDbService._

  def receive = {

    case user:RegisterUser =>
      log.info("processing " + user)
      val currentSender = sender
      val addOrder      = (postgresDbService ? UserDbEntry(user)
    ).mapTo[PostgresDbQueryResult]
      addUser onComplete { response =>
        response match { 
          case Success(response) =>
          currentSender ! response
          case Failure(error) =>
          log.error("Exception: {}", error)
        }
      }

  }

}
  