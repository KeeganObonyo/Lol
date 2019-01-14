package com.keeganosala.lol.market

import scala.concurrent.duration._
import scala.language.postfixOps

import akka.actor.Props
import akka.testkit.TestProbe

import com.keeganosala._

import lol.test._

import lol.market._

import DataAccessService._

import computations.ComputationsService._


class DataAccessServiceSpec extends LolTestService {

  val computationsServiceProbe = TestProbe()

  val dataAccessService = system.actorOf(Props(new DataAccessService{
      override def createComputationsService  = computationsServiceProbe.ref
  }))

  "The DataAccessService" must {
    "send a request to the broker and return a GraphDataRequestResponse" in {
      dataAccessService ! GraphDataRequest("MSFT")
      computationsServiceProbe.expectMsg(ComputationsServiceGraphRequest("MSFT"))
      computationsServiceProbe.reply(
        ComputationsServiceGraphResponse(
          Map("2019-01-09 14:41:00" -> Map(
              "2. high"   -> "104.3000",
              "3. low"    -> "104.2500",
              "5. volume" -> "31102",
              "1. open"   -> "104.3000",
              "4. close"  -> "104.2800"
            )
          )      
        ))
      expectMsg(
        GraphDataRequestResponse(
          Map("2019-01-09 14:41:00" -> Map(
              "2. high"   -> "104.3000",
              "3. low"    -> "104.2500",
              "5. volume" -> "31102",
              "1. open"   -> "104.3000",
              "4. close"  -> "104.2800"
            )
          )      
        ))

      computationsServiceProbe.expectNoMessage(100 millis)
    }
    "send a request to the broker and return a VolatilityAnalysisResponse" in {
      dataAccessService ! VolatilityAnalysisRequest("MSFT")
      computationsServiceProbe.expectMsg(ComputationsServiceVolatilityRequest("MSFT"))
      computationsServiceProbe.reply(
        ComputationsServiceVolatilityResponse(
           Map(
              "high"   -> 0.0,
              "low"    -> 0.0,
              "volume" -> 0.0,
              "open"   -> 0.0,
              "close"  -> 0.0
            )
        ))
      expectMsg(
        VolatilityAnalysisResponse(
           Map(
              "high"   -> 0.0,
              "low"    -> 0.0,
              "volume" -> 0.0,
              "open"   -> 0.0,
              "close"  -> 0.0
            )
        ))
      computationsServiceProbe.expectNoMessage(100 millis)
    }
  }
}

