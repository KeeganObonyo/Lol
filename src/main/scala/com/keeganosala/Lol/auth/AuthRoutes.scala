package com.keeganosala.Lol
package auth 

import java.util.concurrent.TimeUnit

import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.{ Directive1, Route,Directives }
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.pattern._
import users._
import models._
import auth.AuthenticationActor._

import scala.util.{Failure,Success}
import scala.concurrent.ExecutionContext.Implicits.global

trait AuthRoutes extends JsonSupport with AuthenticationLogic{

  def login = post {
    entity(as[LoginRequest]) { 
      loginreq =>
      onComplete((authenticationActor ? GetUserInstance(ModelTorow(loginreq))).mapTo[UserInstance]) {
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