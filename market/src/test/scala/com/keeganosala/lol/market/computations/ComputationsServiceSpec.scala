package com.keeganosala.lol.market
package computations

import scala.concurrent.duration._
import scala.language.postfixOps

import akka.actor.Props
import akka.testkit.TestProbe

import com.keeganosala._

import lol.test._

import lol.market._

import computations.ComputationsService._

import gateway.AlphavantageGateway._


class ComputationsServiceSpec extends LolTestService {

  val alphavantageGatewayServiceProbe = TestProbe()

  val computationsService = system.actorOf(Props(new ComputationsService{
      override def createAlphavantageGateway  = alphavantageGatewayServiceProbe.ref
  }))

  "The ComputationsService" must {
    "request for data from the broker and return a ComputationsServiceGraphResponse" in {
      computationsService ! ComputationsServiceGraphRequest
      alphavantageGatewayServiceProbe.expectMsg(AlphavantageDataGatewayRequest)
      alphavantageGatewayServiceProbe.reply(
        AlphavantageDataGatewayResponse(
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

      alphavantageGatewayServiceProbe.expectNoMessage(100 millis)
    }
    "request for data from the broker and return a ComputationsServiceVolatilityResponse" in {
      computationsService ! ComputationsServiceVolatilityRequest
      alphavantageGatewayServiceProbe.expectMsg(AlphavantageDataGatewayRequest)
      alphavantageGatewayServiceProbe.reply(
        AlphavantageDataGatewayResponse(
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
        ComputationsServiceVolatilityResponse(
           Map(
              "high"   -> 0.0,
              "low"    -> 0.0,
              "volume" -> 0.0,
              "open"   -> 0.0,
              "close"  -> 0.0
            )
        ))

      alphavantageGatewayServiceProbe.expectNoMessage(100 millis)
    }
  }
}

