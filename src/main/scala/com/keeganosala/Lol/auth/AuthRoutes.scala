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

import users.UserRegistryActor._
import akka.pattern.ask
import scala.concurrent.duration._
import akka.util.Timeout
import scala.util.{Failure,Success}
import akka.actor.{ ActorRef, Actor, ActorLogging, Props, ActorSystem}
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import akka.http.scaladsl.unmarshalling.Unmarshal

trait AuthRoutes extends JsonSupport with AuthenticationLogic{

  def login = post {
    entity(as[LoginRequest]) { 
      loginreq =>
      val token = returnToken(loginreq)
        respondWithHeader(RawHeader("Access-Token", token)) {
          complete(StatusCodes.OK)
        }
      }
    }

  lazy val authRoutes: Route = path("auth"){
  	login ~ checkvalidity
  }
}
