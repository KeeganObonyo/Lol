package com.keeganosala.lol.core

import scala.concurrent.duration._
import scala.language.postfixOps

import akka.actor.Props
import akka.testkit.TestProbe

import com.keeganosala._

import lol.core.db.postgres.PostgresDbQueryResult
 
import lol.core.db.postgres.service.PostgresDbService._

import lol.test._

import lol.core.writer._

import WriteToDbService._

class WriteToDbServiceSpec extends LolTestService {

  val postgresDbServiceProbe = TestProbe()

  val writeToDbService = system.actorOf(Props(new WriteToDbService{
      override def createPostgresDbService = postgresDbServiceProbe.ref
  }))

  "The WriteToDbService" must {
    "Add a new user to the database" in {
      writeToDbService ! RegisterUser(
        email    = "user@gmail.com",
        name     = "user",
        password = "password"
      )
      postgresDbServiceProbe.expectMsg(UserDbEntryServiceRequest(
        email    = "user@gmail.com",
        name     = "user",
        password = "password"
      ))
      postgresDbServiceProbe.reply(PostgresDbQueryResult(
        rowsAffected = 23))
      expectMsg(PostgresDbQueryResult(
        rowsAffected = 23))

      postgresDbServiceProbe.expectNoMessage(100 millis)
    }
  }
}

