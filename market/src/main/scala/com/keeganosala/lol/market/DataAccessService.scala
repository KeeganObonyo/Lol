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
	case class GraphDataRequest(
		symbol:String
	)

	case class VolatilityAnalysisRequest(
		symbol:String
	)

	case class GraphDataRequestResponse(
    	data : Map[String,Map[String,String]]
	)
	case class VolatilityAnalysisResponse(
		data : Map[String,Double]
	)
}

class DataAccessService extends Actor
	with ActorLogging 
	with Computations {

	implicit val system					= context.system

 	implicit val materializer           = ActorMaterializer()

  	def createComputationsService 		= system.actorOf(Props[ComputationsService])

  	private val computationsService 	= createComputationsService

  	implicit val timeout    			= Timeout(LolConfig.httpRequestTimeout)
  	
	import DataAccessService._ 
	import context.dispatcher

	def receive: Receive = {
		case request:GraphDataRequest => 
			val currentSender = sender
      		log.info("processing " + GraphDataRequest)
      		val obtainData = (computationsService ? ComputationsServiceGraphRequest(request.symbol)
      	).mapTo[ComputationsServiceGraphResponse] 
      		obtainData onComplete {
		  		case Success(graphdata) => 
		  			currentSender ! GraphDataRequestResponse(
		  				data = graphdata.data
		  			)
		  		case Failure(e) => 
      				log.info("Error obtaining analysed data from service")
		  			currentSender ! Nil
			}
		case request:VolatilityAnalysisRequest => 
			val currentSender = sender
      		log.info("processing " + VolatilityAnalysisRequest)
	      	val obtainData = (computationsService ? ComputationsServiceVolatilityRequest(request.symbol)
	    ).mapTo[ComputationsServiceVolatilityResponse] 
	      	obtainData onComplete {
		  		case Success(volatilitydata) => 
		  			currentSender ! VolatilityAnalysisResponse(
		  				data = volatilitydata.data
		  			)
		  		case Failure(e) => 
	      			log.info("Error obtaining analysed data from service")
					currentSender ! Nil
			}
	}
}