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
		val currentSender = sender
		case GetGraph => 
      		log.info("processing " + GetGraph)
			val graphData = obtainGraphData
			currentSender ! graphData
		case GetVolatility => 
      		log.info("processing " + GetVolatility)
			val volatility = obtainMarketVolatility
			currentSender ! volatility
	}
}