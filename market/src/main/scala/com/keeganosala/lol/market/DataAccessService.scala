package com.keeganosala.lol.market

import scala.util.{ Failure, Success }

import akka.actor.{ Actor, ActorLogging, Props }
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout

import com.keeganosala._

import lol.core.config.LolConfig

import lol.market.computations._

import ComputationsService._

object DataAccessService {
	case object GraphDataRequest
	case object VolatilityAnalysisRequest
}

class DataAccessService extends Actor
	with ActorLogging 
	with Computations {

	implicit val system = context.system

  	implicit val materializer: ActorMaterializer = ActorMaterializer()

  	def createComputationsService 		= system.actorOf(Props[ComputationsService])

  	private val computationsService 	= createComputationsService

  	implicit val timeout    = Timeout(LolConfig.httpRequestTimeout)
  	
	import DataAccessService._ 
	import context.dispatcher

	def receive: Receive = {
		case GraphDataRequest => 
			val currentSender = sender
      		log.info("processing " + GraphDataRequest)
      		val obtainData = (computationsService ? ComputationsServiceGraphRequest
      	).mapTo[ComputationsServiceGraphResponse] 
      		obtainData onComplete {
		  		case Success(graphdata) => 
		  			currentSender ! graphdata.data
		  		case Failure(e) => 
      				log.info("Error obtaining analysed data from service")
		  			currentSender ! Nil
			}
		case VolatilityAnalysisRequest => 
			val currentSender = sender
      		log.info("processing " + VolatilityAnalysisRequest)
	      	val obtainData = (computationsService ? ComputationsServiceVolatilityRequest
	    ).mapTo[ComputationsServiceVolatilityResponse] 
	      	obtainData onComplete {
		  		case Success(volatilitydata) => 
					currentSender ! volatilitydata.data
		  		case Failure(e) => 
	      			log.info("Error obtaining analysed data from service")
					currentSender ! Nil
			}
	}
}