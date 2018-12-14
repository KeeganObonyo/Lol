package com.keeganosala.lol.market
package gateway

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{ Failure, Success }
import scala.concurrent.Future

import akka.actor.{ Actor, ActorLogging }
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer

import com.keeganosala._

import lol.core.config.LolConfig

import marshalling.AlphavantageMarshalling

object DataAccessService {

	case object GetData
}

class DataAccessService extends Actor 
	with ActorLogging 
	with AlphavantageMarshalling {

	implicit val system 	  = context.system

 	implicit val materializer = ActorMaterializer()

  	val url             	  = LolConfig.brokerUrl

	import DataAccessService._
	import context.dispatcher

	def receive: Receive = {
		case GetData=>
			log.info("processing" + GetData)
			val currentSender = sender
    		val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))
		    responseFuture onComplete {
		    	case Success(response)=>
		    		log.info("Successfully retrieved data from alphavantage")
		      		val alphavantageData = Unmarshal(response.entity).to[AlphavantageData]
					currentSender ! alphavantageData
				case Failure(error)=>
		    		log.info("Failure retrieving data from alphavantage")
					currentSender ! Nil
		}
	}
}
