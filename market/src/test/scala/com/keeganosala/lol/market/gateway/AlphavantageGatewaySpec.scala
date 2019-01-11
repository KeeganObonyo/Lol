package com.keeganosala.lol.market
package gateway
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{ Failure, Success }
import scala.concurrent.duration._
import scala.concurrent.Future
import scala.language.postfixOps

import akka.actor.Props
import akka.http.scaladsl.marshalling.Marshal

import akka.http.scaladsl.model._
import akka.testkit._

import com.keeganosala._

import lol.market.gateway._

import lol.test._

import AlphavantageGateway._

import marshalling._

class AlphavantageGatewaySpec extends LolTestHttpService
    with AlphavantageMarshalling {

  val alphavantageGateway = system.actorOf(Props(new AlphavantageGateway {
    override def sendHttpRequest(req: HttpRequest) =
      Future.successful(getStringHttpResponse(req))
  }))
  "The AlphavantageGateway" must {
  	"process a request and return market data" in {
      alphavantageGateway ! AlphavantageDataGatewayRequest
      expectMsg(FiniteDuration(20, "seconds"),
      AlphavantageDataGatewayResponse(
          Map("2019-01-09 14:41:00" -> Map(
              "2. high"   -> "104.3000",
              "3. low"    -> "104.2500",
              "5. volume" -> "31102",
              "1. open"   -> "104.3000",
              "4. close"  -> "104.2800"
            )
          )
        )
      )
    }
  }
  override def getStringHttpResponseImpl(
    data:String,
    uri: Uri
  ) = {
        val response = Marshal(
          Map("Time Series (1min)"-> Map(
                  "2019-01-09 14:41:00" -> Map(
                    "2. high"   -> "104.3000",
                    "3. low"    -> "104.2500",
                    "5. volume" -> "31102",
                    "1. open"   -> "104.3000",
                    "4. close"  -> "104.2800"
                )
        ))).to[ResponseEntity]
    HttpResponse(
      status = StatusCodes.OK,
      entity = Await.result(
        response,
        1.second
      ))
  } 
}

