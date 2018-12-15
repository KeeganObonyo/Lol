package com.keeganosala.lol.core
package query

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{ Failure, Success }

import akka.actor.{ Actor, ActorLogging, Props }
import akka.pattern.ask
import akka.util.Timeout
import akka.event.Logging

import com.keeganosala._

import lol.core.config.LolConfig

import lol.core.db.postgres.service._

import PostgresDbService._

object QueryService {

  case object UsersFetchQueryServiceRequest

  case class UsersFetchQueryServiceResponse(
    users:List[User]
  )

  case class SingleUserFetchQueryServiceRequest(
    id:String
  )

  case class SingleUserFetchQueryServiceResponse(
    id:String,
    name:String,
    email:String
  )

  case class User(
    id:String,
    name:String,
    email:String
  )
}

class QueryService extends Actor
    with ActorLogging{

  implicit val actorSystem       = context.system

  implicit val timeout           = Timeout(LolConfig.queryTimeout)

  def createPostgresDbService    = context.actorOf(Props[PostgresDbService])

  private val postgresDbService  = createPostgresDbService

  import QueryService._

  def receive = {

    case UsersFetchQueryServiceRequest =>
      log.info("processing " + UsersFetchQueryServiceRequest)
      val currentSender = sender
      val userlist      = (postgresDbService ? UsersFetchDbServiceRequest
    ).mapTo[List[User]]
      userlist onComplete { response =>
        response match { 
          case Success(response) =>
          currentSender ! UsersFetchQueryServiceResponse(
            users = response
        )
          case Failure(error) =>
          log.error("Exception: {}", error)
          currentSender ! UsersFetchQueryServiceResponse(
            users       = List()
          )
        }
      }
    case getuser:SingleUserFetchQueryServiceRequest =>
      log.info("processing" + getuser)
      val currentSender = sender
      val user          = (postgresDbService ? SingleUserFetchDbServiceRequest
    ).mapTo[User]
      user onComplete { response =>
        response match { 
          case Success(response) =>
          currentSender ! SingleUserFetchQueryServiceResponse(
            id    = response.id,
            name  = response.name,
            email = response.email
        )
          case Failure(error) =>
          log.error("Exception: {}", error)
          currentSender ! None
        }
      }
  }

}
  