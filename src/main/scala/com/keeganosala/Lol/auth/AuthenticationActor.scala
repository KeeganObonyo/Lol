package com.keeganosala.Lol
package auth

import akka.actor.{ ActorRef, Actor, ActorLogging, Props, ActorSystem}
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import akka.pattern.ask
import models._
import users._
import akka.util.Timeout
import java.io.IOException
import java.io.FileNotFoundException
import akka.pattern.pipe

final case class LoginRequest(email: String, password: String)

object AuthenticationActor {

	final case class GetUserInstance(request:Array[Any])
}

class AuthenticationActor extends Actor with ActorLogging{

	import AuthenticationActor._

  val databaseinstance = DbTransactions()

	def receive: Receive = {
		case GetUserInstance(request) =>
			databaseinstance.getUserInstance(request).mapTo[UserInstance] pipeTo sender
	}
}
