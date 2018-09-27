package auth 


import java.util.concurrent.TimeUnit

import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.{ Directive1, Route }
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.pattern._

import scala.util.Failure

class AuthenticationLogic {

	import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  	import io.circe.generic.auto._
  	import authentikat.jwt._


	private val tokenExpiryPeriodInDays = 1
	private val secretKey               = "drcfghjuigilfqUIGYO%#^&()&vjgvgv"
	private val header                  = JwtHeader("HS256")


	private def login(credentials:LoginRequest) = {
	    case lr @ LoginRequest("admin", "admin") =>
	        JsonWebToken(header, setClaims(user, tokenExpiryPeriodInDays), secretKey))
	    case LoginRequest(_, _) => Nil
	}

	//method to return JWT token
	private def setClaims(name: User, expiryPeriodInDays: Long) = JwtClaimsSet(
	  Map("user" -> name,
	      "expiredAt" -> (System.currentTimeMillis() + TimeUnit.DAYS
	        .toMillis(expiryPeriodInDays)))
	)

	//method to check validity
	private def authenticated(jwt:String) = {
	    case Some(jwt) if isTokenExpired(jwt) => "Token expired."
	    case Some(jwt) if JsonWebToken.validate(jwt, secretKey) => jwt
	  }

	//method to check JWT expiration
	private def isTokenExpired(jwt: String) = getClaims(jwt) match {
	  case Some(claims) =>
	    claims.get("expiredAt") match {
	      case Some(value) => value.toLong < System.currentTimeMillis() 
	      case None        => false
	    }
	  case None => false
	}

	//convert jwt back to object
	private def getClaims(jwt: String) = jwt match {
	 case JsonWebToken(_, claims, _) => claims.asSimpleMap.toOption
	 case _                          => None
	}

	def apply(): Authentication {

		new Authentication()
	}
}

class Authentication {

	import Authentication._
}