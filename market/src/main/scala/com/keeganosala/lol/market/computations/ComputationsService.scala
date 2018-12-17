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

import DataAccessService._

object ComputationsService {
	case object GetGraph
	case object GetVolatility
}

class ComputationsService extends Actor
	with ActorLogging 
	with Computations {

	implicit val system = context.system

  	implicit val materializer: ActorMaterializer = ActorMaterializer()

  	val dataAccessService 	= system.actorOf(Props[DataAccessService], "dataAccessService")

  	implicit val timeout    = Timeout(LolConfig.httpRequestTimeout)
  	
	import ComputationsService._ 
	import context.dispatcher

	def receive: Receive = {
		case GetGraph => 
			val currentSender = sender
      		log.info("processing " + GetGraph)
      		val obtainData = (dataAccessService ? GetData).mapTo[AlphavantageDataDataAccessResponse] 
      		obtainData onComplete {
		  		case Success(data) => 
		  			currentSender ! data.`Time Series (1min)`
		  		case Failure(e) => 
      				log.info("Error obtaining data service")
		  			currentSender ! Nil
			}
		case GetVolatility => 
			val currentSender = sender
      		log.info("processing " + GetVolatility)
	      	val obtainData = (dataAccessService ? GetData).mapTo[AlphavantageDataDataAccessResponse] 
	      	obtainData onComplete {
		  		case Success(data) => 
		  			val volatility = calculateVolatility(new AlphavantageData(timedata = data `Time Series (1min)`))
					currentSender ! volatility
		  		case Failure(e) => 
	      			log.info("Error obtaining data from service")
					currentSender ! Nil
			}
	}
}