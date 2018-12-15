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
import lol.core.db.postgres.service.PostgresDbService
import lol.core.db.postgres.PostgresDbQueryResult


object WriteToDbService {

  case class RegisterUser(
    email: String,
    name: String,
    password:String
  )

  case class DeleteUser(
    id:String
  )
}

class WriteToDbService extends Actor
    with ActorLogging{


  implicit val actorSystem       = context.system

  implicit val timeout           = Timeout(LolConfig.queryTimeout)

  def createPostgresDbService    = context.actorOf(Props[PostgresDbService])

  private val postgresDbService  = createPostgresDbService

  import WriteToDbService._
  import PostgresDbService._

  def receive = {

    case user:RegisterUser =>
      log.info("processing " + user)
      val currentSender = sender
      val addUser       = (postgresDbService ? UserDbEntryServiceRequest(
        email    = user.email,
        name     = user.name,
        password = user.password
      )
    ).mapTo[PostgresDbQueryResult]
      addUser onComplete { response =>
        response match { 
          case Success(response) =>
          currentSender ! response
          case Failure(error) =>
          log.error("Exception: {}", error)
        }
      }

    case deleteuser:DeleteUser =>
      log.info("processing " + deleteuser)
      val currentSender = sender
      val addUser       = (postgresDbService ? UserDeleteDbServiceRequest(
        id    = deleteuser.id
      )
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
  