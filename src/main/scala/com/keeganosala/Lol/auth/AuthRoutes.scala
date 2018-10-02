package com.keeganosala.Lol
package auth 

import java.util.concurrent.TimeUnit
import scala.concurrent.duration._
import scala.util.{Failure,Success}
import scala.concurrent.ExecutionContext.Implicits.global

import akka.util.Timeout
import akka.actor.{ ActorRef, Actor, ActorLogging, Props, ActorSystem}
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.{ Directive1, Route,Directives }
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.pattern._

import users._
import models._
import auth.UserInstanceActor._



trait AuthRoutes extends JsonSupport with AuthenticationLogic{

  implicit def system: ActorSystem

  def userInstanceActor: ActorRef

  implicit lazy val time = Timeout(5.seconds)


  def ModelTorow(loginrequest:LoginRequest): Array[Any] = {
      Array(
        loginrequest.email,
        loginrequest.password
      )
    }

  def login = post {
    entity(as[LoginRequest]) { 
      loginreq =>
      onComplete((userInstanceActor ? GetUserInstance(ModelTorow(loginreq))).mapTo[UserInstance]) {
        case Success(user) => 
        respondWithHeader(RawHeader("Access-Token", returnToken(user))) {
          complete(StatusCodes.OK)
        }
        case Failure(exception) => 
          complete(StatusCodes.BadRequest)
      }
    }
  }

  lazy val authRoutes: Route = path("auth"){
  	login ~ checkvalidity
  }
}