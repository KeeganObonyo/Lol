package com.keeganosala.lol.core
package util.auth 

import java.util.concurrent.TimeUnit

import scala.collection.mutable.ListBuffer
import scala.concurrent.{ Await, Future }
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{ Failure, Success }

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.{ Directive1, Route,Directives }
import akka.http.scaladsl.server.Directives._
import akka.pattern._
import akka.pattern.ask

import com.keeganosala._

import util.CorsHandler

object AuthenticationLogic extends AuthenticationLogic

trait AuthenticationLogic {

  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._
  import authentikat.jwt._

  private val tokenExpiryPeriodInDays = 1
  private val header                  = JwtHeader("HS256")
  private val secretKey               = "sgdhfjghjk[p';hgubiugh67@@#&*()$"

  private def returnToken(user:UserInstance): String ={
    val claims = setClaims(user, tokenExpiryPeriodInDays)
    JsonWebToken(header, claims, secretKey)
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

