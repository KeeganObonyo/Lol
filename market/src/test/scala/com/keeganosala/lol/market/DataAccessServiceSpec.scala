package com.keeganosala.lol.market

import scala.concurrent.duration._
import scala.language.postfixOps

import akka.actor.Props
import akka.testkit.TestProbe

import com.keeganosala._

import elmer.test._

import elmer.market._

import DataAccessService._

import gateway.AlphavantageGateway._


class DataAccessServiceSpec extends LolTestService {

  val foodOrderGatewayServiceProbe = TestProbe()

  val foodOrderService = system.actorOf(Props(new FoodOrderService{
      override def createFoodOrderGateway  = foodOrderGatewayServiceProbe.ref
  }))

  val foodName = FoodName.Ugali
  val quantity = 4

  "The DataAccessService" must {
    "send the food order to the broker and return a FoodOrderServiceResponse" in {

      foodOrderService ! FoodOrderServiceRequest(
        name     = foodName,
        quantity = quantity
      )

      foodOrderGatewayServiceProbe.expectMsg(FoodOrderGatewayRequest(
        name        = foodName,
        quantity    = quantity
      ))
      foodOrderGatewayServiceProbe.reply(FoodOrderGatewayResponse(
        status      = OrderRequestStatus.Accepted,
        description = "request Accepted"
      ))

      expectMsg(FoodOrderServiceResponse(
        status       = OrderRequestStatus.Accepted,
        description  = "request Accepted"
      ))

      foodOrderGatewayServiceProbe.expectNoMessage(100 millis)
    }
  }
}

