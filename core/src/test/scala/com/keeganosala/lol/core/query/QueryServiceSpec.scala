package com.keeganosala.lol.core

import scala.concurrent.duration._
import scala.language.postfixOps

import akka.actor.Props
import akka.testkit.TestProbe

import com.keeganosala._

import lol.core.db.postgres.PostgresDbQueryResult
 
import lol.core.db.postgres.service.PostgresDbService._

import lol.test._

import lol.core.query._

import QueryService._

class QueryServiceSpec extends LolTestService {

  val postgresDbServiceProbe = TestProbe()

  val queryService = system.actorOf(Props(new QueryService{
      override def createPostgresDbService = postgresDbServiceProbe.ref
  }))

  "The QueryService" must {
    "Fetch registered users from database" in {
      queryService ! UsersFetchQueryServiceRequest
      postgresDbServiceProbe.expectMsg(UsersFetchDbServiceRequest)
      postgresDbServiceProbe.reply(List(User(
        id    = 1,
        name  = "user",
        email = "user@gmail.com"
      )))
      expectMsg(UsersFetchQueryServiceResponse(List(User(
        id    = 1,
        name  = "user",
        email = "user@gmail.com"
      ))))

      postgresDbServiceProbe.expectNoMessage(100 millis)
    }
    "Retrieve a single user from the database" in {
      queryService ! SingleUserFetchQueryServiceRequest(
          id = 3
      )
      postgresDbServiceProbe.expectMsg(SingleUserFetchDbServiceRequest(
        id = 3
      ))
      postgresDbServiceProbe.reply(Some(User(
          id    = 1,
          name  = "name",
          email = "name@gmail.com"
      )))
      expectMsg(SingleUserFetchQueryServiceResponse(
          id    = 1,
          name  = "name",
          email = "name@gmail.com"
      ))

      postgresDbServiceProbe.expectNoMessage(100 millis)
    }
  }
}

