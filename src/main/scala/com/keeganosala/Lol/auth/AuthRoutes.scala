package com.keeganosala.Lol
package auth 

import java.util.concurrent.TimeUnit

import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.{ Directive1, Route }
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


trait AuthRoutes extends JsonSupport {

  implicit def system: ActorSystem

  def authenticationActor: ActorRef


  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._
  import authentikat.jwt._

  private val tokenExpiryPeriodInDays = 1
  private val secretKey               = "sgdhfjghjk[p';hgubiugh67@@#&*()$"
  private val header                  = JwtHeader("HS256")

  val databaseinstance = DbTransactions()

  implicit lazy val time = Timeout(5.seconds)

  private def ModelTorow(user:LoginRequest): Array[Any] = {
      Array(
        user.email,
        user.password
      )
    }

  private def login = post {
    entity(as[LoginRequest]) { loginreq =>
    //   databaseinstance.getUserInstance(loginreq) match {
    //   case Some(user) =>
    //     val claims = setClaims(user, tokenExpiryPeriodInDays)
    //     respondWithHeader(RawHeader("Access-Token", JsonWebToken(header, claims, secretKey))) {
    //       complete(StatusCodes.OK)
    //     }
    //   case _ : Throwable => complete(StatusCodes.Unauthorized)
    //   }
    // }
      val user = (authenticationActor ? GetUserInstance(ModelTorow(loginreq))).mapTo[UserInstance]
      if (user.id == 0) {
        complete(StatusCodes.Unauthorized)
      }else{
        val claims = setClaims(user, tokenExpiryPeriodInDays)
        respondWithHeader(RawHeader("Access-Token", JsonWebToken(header, claims, secretKey))) {
          complete(StatusCodes.OK)
        }
      }
    }
  }

  private def checkvalidity = get {
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

  private def setClaims(user: UserInstance, expiryPeriodInDays: Long) = JwtClaimsSet(
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

  lazy val authRoutes: Route = path("auth"){
  	login ~ checkvalidity
  }
}
