package com.keeganosala.Lol
package data

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import akka.actor.{ ActorRef, ActorSystem, ActorRefFactory }

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future
import scala.util.{ Failure, Success }
import akka.http.scaladsl.model._, headers.HttpEncodings
import akka.http.scaladsl.unmarshalling.Unmarshal

case class AlphavantageData(
  // `Meta Data`: Map[String,String],
  `Time Series (1min)`: Map[String,Map[String,String]]
)

case class AlphavantageData2(
    timedata : Map[String,Map[String,String]]
)


trait Data extends AutoMarshalling {

	implicit val system: ActorSystem

	implicit def materializer: ActorMaterializer

  val url = "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=MSFT&interval=1min&apikey=KLMH2VFJ0LCFNOX5"

	def getData():Future[AlphavantageData] = {
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))

    responseFuture flatMap  { response =>
      Unmarshal(response.entity).to[AlphavantageData] map { alphavantagedata =>
          alphavantagedata
      }
    }

	}
}