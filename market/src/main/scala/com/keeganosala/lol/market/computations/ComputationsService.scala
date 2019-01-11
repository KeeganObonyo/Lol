package com.keeganosala.lol.market
package computations

import scala.util.{ Failure, Success }

import akka.actor.{ Actor, ActorLogging, Props }
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout

import com.keeganosala._

import lol.core.config.LolConfig

import lol.market.gateway._

import AlphavantageGateway._

object ComputationsService {
	case object ComputationsServiceGraphRequest

	case object ComputationsServiceVolatilityRequest

	case class ComputationsServiceGraphResponse(
    	data : Map[String,Map[String,String]]
	)

	case class ComputationsServiceVolatilityResponse(
		data : Map[String,Double]
	)

}

class ComputationsService extends Actor
	with ActorLogging 
	with Computations {

	implicit val system 				= context.system

  	def createAlphavantageGateway 		= system.actorOf(Props[AlphavantageGateway])

  	private val alphavantageGateway 	= createAlphavantageGateway

  	implicit val timeout    			= Timeout(LolConfig.httpRequestTimeout)
  	
	import ComputationsService._ 
	import context.dispatcher

	def receive: Receive = {
		case ComputationsServiceGraphRequest => 
			val currentSender = sender
      		log.info("processing " + ComputationsServiceGraphRequest)
      		val obtainData = (alphavantageGateway ? AlphavantageDataGatewayRequest).mapTo[AlphavantageDataGatewayResponse] 
      		obtainData onComplete {
		  		case Success(data) => 
		  			currentSender ! ComputationsServiceGraphResponse(
		  				data = data.`Time Series (1min)`
		  			)
		  		case Failure(e) => 
      				log.info("Error obtaining data service")
		  			currentSender ! Nil
			}
		case ComputationsServiceVolatilityRequest => 
			val currentSender = sender
      		log.info("processing " + ComputationsServiceVolatilityRequest)
	      	val obtainData = (alphavantageGateway ? AlphavantageDataGatewayRequest).mapTo[AlphavantageDataGatewayResponse] 
	      	obtainData onComplete {
		  		case Success(data) => 
		  			val volatility = calculateVolatility(new AlphavantageData(timedata = data `Time Series (1min)`))
					currentSender ! ComputationsServiceVolatilityResponse(
						data = volatility
					)
		  		case Failure(e) => 
	      			log.info("Error obtaining data from service")
					currentSender ! Nil
			}
	}
}