package com.keeganosala.Lol
package data

import akka.actor.{ Actor, ActorLogging }
import akka.pattern.pipe
import akka.stream.ActorMaterializer
import akka.util.Timeout

object DataAccessService {

	case object GetData
}

class DataAccessService extends Actor 
	with ActorLogging 
	with AlphavantageMarshalling {

	implicit val system = context.system

  	val url             = LolConfig.brokerUrl

	import DataAccessService._
	import context.dispatcher

	def receive: Receive = {
		log.info("processing" + request)
		val currentSender = sender
		case request:GetData=>
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
