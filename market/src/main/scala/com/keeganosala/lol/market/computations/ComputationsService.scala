package com.keeganosala.lol.market
package computations

import akka.actor.{ Actor, ActorLogging, Props }
import akka.util.Timeout
import akka.pattern.pipe

object ComputationsService {
	case object GetGraph
	case object GetVolatility
}

class ComputationsService extends Actor
	with ActorLogging 
	with Computations {

	implicit val system = context.system

	import ComputationsService._ 
	import context.dispatcher

	def receive: Receive = {
		case GetGraph => 
			val currentSender = sender
      		log.info("processing " + GetGraph)
			currentSender ! obtainGraphData
		case GetVolatility => 
			val currentSender = sender
      		log.info("processing " + GetVolatility)
			currentSender ! obtainMarketVolatility
	}
}