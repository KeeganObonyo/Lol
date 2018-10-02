package com.keeganosala.Lol
package auth 

import java.util.concurrent.TimeUnit

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.{ Directive1, Route,Directives }
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.pattern._

import akka.pattern.ask
import scala.util.{Failure,Success}
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.mutable.ListBuffer

import users._
import models._

trait AuthenticationLogic {

  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._
  import authentikat.jwt._

  private val tokenExpiryPeriodInDays = 1
  private val header                  = JwtHeader("HS256")
  private val secretKey               = "sgdhfjghjk[p';hgubiugh67@@#&*()$"

  def returnToken(user:UserInstance): String ={
    val claims = setClaims(user, tokenExpiryPeriodInDays)
    JsonWebToken(header, claims, secretKey)
  }

  def checkvalidity = get {
    authenticated { claims =>
      complete(s"authentication still valid!")
    }
  }

  def authenticated: Directive1[Map[String, Any]] =
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

