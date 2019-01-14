package com.keeganosala.lol.core
package util.auth

import scala.concurrent.duration._
import scala.language.postfixOps

import akka.actor.Props
import akka.testkit.TestProbe

import com.keeganosala._

import lol.core.db.postgres.service.PostgresDbService._

import lol.core.util.auth._

import lol.test._

import AuthenticationService._

class AuthenticationServiceSpec extends LolTestService {

  val postgresDbServiceProbe = TestProbe()

  val authenticationService  = system.actorOf(Props(new AuthenticationService{
      override def createPostgresDbService = postgresDbServiceProbe.ref
  }))

  "The AuthenticationService" must {
    "provide an Authentication token provided the correct credentials" in {
      authenticationService ! AuthenticateUserServiceRequest(
        email    = "name@gmail.com",
        password = "password"
      )
      postgresDbServiceProbe.expectMsg(UserDbRetrieveServiceRequest(
        email    = "name@gmail.com",
        password = "password"
      ))
      postgresDbServiceProbe.reply(Some(UserDbRetrieveServiceResponse(
        id       = 1,
        name     = "user",
        email    = "user@gmail.com",
        password = "password"
      )))
      expectMsgType[String]

      postgresDbServiceProbe.expectNoMessage(100 millis)
    }
  }
}

