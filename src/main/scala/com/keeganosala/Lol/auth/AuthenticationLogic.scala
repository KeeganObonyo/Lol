package com.keeganosala.Lol
package auth 

import java.util.concurrent.TimeUnit
import scala.util.{Try, Success, Failure}

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
import scala.collection.mutable.ListBuffer

trait AuthenticationLogic {

  implicit def system: ActorSystem

  def authenticationActor: ActorRef

  implicit lazy val time = Timeout(5.seconds)

  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._
  import authentikat.jwt._

  private val tokenExpiryPeriodInDays = 1
  private val header                  = JwtHeader("HS256")
  private val secretKey               = "sgdhfjghjk[p';hgubiugh67@@#&*()$"

  def ModelTorow(loginrequest:LoginRequest): Array[Any] = {
      Array(
        loginrequest.email,
        loginrequest.password
      )
    }

  def returnToken(user:UserInstance): String ={
    JsonWebToken(header, setClaims(user, tokenExpiryPeriodInDays), secretKey)
  }

  def checkvalidity = get {
    authenticated { claims =>
      complete(s"User ${claims.getOrElse("user", "")} authentication still valid!")
    }
  }

  private def authenticated: Directive1[Map[String, Any]] =
    optionalHeaderValueByName("Authorization").flatMap {
      case Some(jwt) if isTokenExpired(jwt) =>
        complete(StatusCodes.Unauthorized -> "Token expired.")

      case Some(jwt) if JsonWebToken.validate(jwt, secretKey) =>
        provide(getClaims(jwt).getOrElse(Map.empty[String, Any]))

      case _ => complete(StatusCodes.Unauthorized)
    }

  def setClaims(user: UserInstance, expiryPeriodInDays: Long) = JwtClaimsSet(
    Map("user" -> user,
        "expiredAt" -> (System.currentTimeMillis() + TimeUnit.DAYS
          .toMillis(expiryPeriodInDays)))
  )

  private def getClaims(jwt: String) = jwt match {
    case JsonWebToken(_, claims, _) => claims.asSimpleMap.toOption
    case _                          => None
  }

  private def isTokenExpired(jwt: String) = getClaims(jwt) match {
    case Some(claims) =>
      claims.get("expiredAt") match {
        case Some(value) => value.toLong < System.currentTimeMillis()
        case None        => false
      }
    case None => false
  }

}
