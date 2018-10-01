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
    val claims = setClaims(user, tokenExpiryPeriodInDays)
    JsonWebToken(header, claims, secretKey)
  }

  def checkvalidity = get {
    authenticated { claims =>
      complete(s"authentication still valid!")
    }
  }

  private def authenticated: Directive1[Map[String, Any]] =
    optionalHeaderValueByName("Authorization").flatMap {
      case Some(jwt) if JsonWebToken.validate(jwt, secretKey) =>
        if (isTokenExpired(jwt)) {
            complete(StatusCodes.Unauthorized -> "Token expired.")
        }else{
            provide(Map("Authorization" -> jwt))
        }
      case _ => complete(StatusCodes.Unauthorized)
    }

  private def setClaims(user: UserInstance, expiryPeriodInDays: Long) = JwtClaimsSet(
    Map("user" -> user,
        "expiredAt" -> (System.currentTimeMillis() + TimeUnit.DAYS
          .toMillis(expiryPeriodInDays)))
  )

  private def getUserfomtoken(jwt: String) = {
    getClaims(jwt) match {
        case Success(value) => value("user")
        case Failure(_) => false
    }

  }

  private def getClaims(jwt:String) = {
      JsonWebToken.unapply(jwt).get._2.asSimpleMap
  }

  private def isTokenExpired(jwt: String) = {
     getClaims(jwt) match {
        case Success(value) => value("expiredAt").toLong < System.currentTimeMillis()
        case Failure(_) => false
    }
  }

}
