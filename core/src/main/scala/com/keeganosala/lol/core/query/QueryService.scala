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
    users:List[UserDbRetrieve]
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
    ).mapTo[List[UserDbRetrieve]]
      userlist onComplete { response =>
        response match { 
          case Success(response) =>
          currentSender ! response
          case Failure(error) =>
          log.error("Exception: {}", error)
          currentSender ! UsersFetchQueryServiceResponse(
            users       = List()
          )
        }
      }

  }

}
  