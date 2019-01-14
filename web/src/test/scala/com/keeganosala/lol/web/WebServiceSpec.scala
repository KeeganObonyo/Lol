package com.keeganosala.lol.web

import scala.concurrent.duration._

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.{ RouteTestTimeout, ScalatestRouteTest }
import akka.http.scaladsl.server._
import Directives._

import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{ Matchers, WordSpec }

import com.keeganosala._

import lol.core.db.postgres.service.PostgresDbService._

import lol.core.util._

import auth._

class WebServiceSpec extends WordSpec 
  	with Matchers 
  	with ScalaFutures 
	  with ScalatestRouteTest
    with WebService     
    with LolJsonProtocol
    with AuthenticationLogic {

  lazy val routes               = route
  
  val invalidAuthToken          = "ATtkn_InvalidAuthToken"

  val validAuthToken            = retrieveToken(UserDbRetrieveServiceResponse(
      id        = 1,
      name      = "TestUSer",
      email     = "test@gmail.com",
      password  = "dfghjkl;kl;;''"
  ))

  implicit val routeTestTimeout = RouteTestTimeout(FiniteDuration(20, "seconds"))

  "LolWebService" should {
    "Reject a request if the user sends in an invalid token" in {
      Get("/lol/data/get/MSFT") ~> addHeader("Authorization", invalidAuthToken) ~> Route.seal(route) ~> check {
        status shouldEqual StatusCodes.Unauthorized
      }
    }
    "be able to return a slice of market data" in {
      Get("/lol/data/get/MSFT") ~> addHeader("Authorization", validAuthToken) ~> Route.seal(route) ~> check {
        status shouldEqual StatusCodes.OK
      }
    }
    "be able to return analysed market data" in {
      Get("/lol/data/compute/MSFT") ~> addHeader("Authorization", validAuthToken) ~> Route.seal(route) ~> check {
        status shouldEqual StatusCodes.OK
      }
    }
    "be able to return a list of users" in {
      Get("/lol/users") ~> addHeader("Authorization", validAuthToken) ~> Route.seal(route) ~> check {
        status shouldEqual StatusCodes.OK
      }
    }
    "be able to return a single user" in {
      Get("/lol/users/1") ~> addHeader("Authorization", validAuthToken) ~> Route.seal(route) ~> check {
        status shouldEqual StatusCodes.NotFound
      }
    }
    "be able to add a new user" in {
      val user =
      parse("""{"email":"user@gmail.com","name":"TestUSer","password":"password"}""").asInstanceOf[JObject]

      Post("/lol/users", user) ~> routes ~> check {
        status shouldEqual StatusCodes.Created
      }
    }
    "be able to reject an invalid user registration" in {
      val user = 
      parse("""{"name":"TestUSer","password":"password"}""").asInstanceOf[JObject]

      Post("/lol/users", user) ~> Route.seal(route) ~> check {
        status shouldEqual StatusCodes.BadRequest
      }
    }
    "be able to delete a registered user" in {
      Delete("/lol/users/1") ~> addHeader("Authorization", validAuthToken) ~> Route.seal(route) ~> check {
        status shouldEqual StatusCodes.OK
      }
    }
    "leave requests with invalid paths unhandled" in {
      Post("lol/other") ~> route ~> check {
        handled shouldEqual false
      }
    }
  }
}
