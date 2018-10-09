package com.keeganosala.Lol
package data

import akka.actor.{ ActorRef, Actor, ActorLogging, Props, ActorSystem}
import akka.stream.ActorMaterializer
import models._
import users._
import akka.util.Timeout
import akka.pattern.pipe

object ComputationsActor {
	final case object GetGraph
	final case object GetVolatility


}

class ComputationsActor extends Actor with ActorLogging with Computations {

	implicit val system = context.system

  	val dataAccessActor: ActorRef = context.actorOf(Props[DataAccessActor], "dataAccessActor")

  	implicit val materializer = ActorMaterializer()

	import ComputationsActor._ 
	import context.dispatcher

	def receive: Receive = {
		case GetGraph => 
			val graphData = obtainGraphData
			sender ! graphData
		case GetVolatility => 
			val volatility = obtainMarketVolatility
			sender ! volatility
	}
}