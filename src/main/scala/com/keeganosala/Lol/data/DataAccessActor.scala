package com.keeganosala.Lol
package data

import akka.actor.{ ActorRef, Actor, ActorLogging, Props, ActorSystem}
import akka.stream.ActorMaterializer
import models._
import users._
import akka.util.Timeout
import akka.pattern.pipe

object DataAccessActor {

	final case object GetData
}

class DataAccessActor extends Actor with ActorLogging with Data{

	implicit val system = context.system

  	implicit val materializer = ActorMaterializer()

	import DataAccessActor._
	import context.dispatcher

	def receive: Receive = {
		case GetData => getData.mapTo[AlphavantageData] pipeTo sender
	}
}
