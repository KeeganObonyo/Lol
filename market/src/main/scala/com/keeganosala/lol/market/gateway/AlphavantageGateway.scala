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

object AlphavantageGateway {

	case object AlphavantageDataGatewayRequest

	case class AlphavantageDataGatewayResponse(
    `Time Series (1min)`: Map[String,Map[String,String]]
  	)

  	case class AlphavantageDataGatewayErrorResponse(
  		description:String
  	)

}

class AlphavantageGateway extends Actor 
	with ActorLogging 
	with AlphavantageMarshalling {

	implicit val system 	  = context.system

 	implicit val materializer = ActorMaterializer()

  	val url             	  = LolConfig.brokerUrl

	import AlphavantageGateway._
	import context.dispatcher

	def sendHttpRequest(request:HttpRequest):Future[HttpResponse] = Http().singleRequest(request)

	def receive: Receive = {
		case AlphavantageDataGatewayRequest =>
			log.info("processing" + AlphavantageDataGatewayRequest)
			val currentSender  = sender
    		val responseFuture = sendHttpRequest(HttpRequest(
    			method = HttpMethods.GET,
    			uri    = url))
    		
		    responseFuture onComplete {
		    	case Success(response)=>
		    		log.info("Successfully retrieved data from alphavantage")
		      		val alphavantageDataDataAccessResponse = Unmarshal(response.entity).to[AlphavantageDataGatewayResponse]
		      		alphavantageDataDataAccessResponse onComplete{
		      			case Success(data)=>
		    				log.info("Successfully processed data from alphavantage")
							currentSender ! data
						case Failure(error)=>
		    				log.info("Got an error while unmarshalling json data from alphavantage")
							currentSender ! AlphavantageDataGatewayErrorResponse(
								description = "Got an error while unmarshalling json data from alphavantage"
							)
		      		}
				case Failure(error)=>
		    		log.info("Failure retrieving data from alphavantage")
					currentSender ! AlphavantageDataGatewayErrorResponse(
						description = "Failure retrieving data from alphavantage"
					)
		}
	}
}
