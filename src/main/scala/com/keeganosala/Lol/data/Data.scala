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


trait Data {

	implicit val system: ActorSystem

	implicit def materializer: ActorMaterializer

	def getData() {

    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri ="https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=MSFT&interval=1min&apikey=KLMH2VFJ0LCFNOX5"))

    responseFuture
      .onComplete { 
        case Success(response) => 
        	println(response)
        	response.discardEntityBytes() 
        case Failure(_)   => sys.error("something wrong")
      }

	}
}