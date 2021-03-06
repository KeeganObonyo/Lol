package com.keeganosala.lol.core
package util.auth

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

object AuthenticationService {

  case class AuthenticateUserServiceRequest(
    email:String,
    password:String
  )

}

class AuthenticationService extends Actor
    with ActorLogging 
    with AuthenticationLogic {

  implicit val actorSystem       = context.system

  implicit val timeout           = Timeout(LolConfig.queryTimeout)

  def createPostgresDbService    = context.actorOf(Props[PostgresDbService])

  private val postgresDbService  = createPostgresDbService

  import AuthenticationService._

  def receive = {

    case authenticate:AuthenticateUserServiceRequest =>
      log.info("processing " + AuthenticateUserServiceRequest)
      val currentSender = sender
      val userInstance = (postgresDbService ? UserDbRetrieveServiceRequest(
                          email    = authenticate.email,
                          password = authenticate.password
    )).mapTo[Option[UserDbRetrieveServiceResponse]]

      userInstance onComplete { response =>
        response match { 
          case Success(response) =>
          currentSender ! retrieveToken(response.get)
          case Failure(error) =>
          log.error("Exception: {}", error)
          currentSender ! Nil
        }
      }

  }

}
  