package auth

import akka.actor.{ ActorRef, Actor, ActorLogging, Props, ActorSystem}
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import akka.pattern.ask
import models._
import akka.util.Timeout
import java.io.IOException
import java.io.FileNotFoundException
import akka.pattern.pipe
import auth.Authentication

final case class LoginRequest(name: String, password: String)

final case class Token(token: String)

final case class AuthCondition(token:Map[String,String])


object AuthenticationActor {

	final case class AuthenticateUser(request:LoginRequest)

	final case class ValidateAuth(request:AuthCondition)

}

class AuthenticationActor extends Actor with ActorLogging{

	import AuthenticationActor._

	authentication = Authentication()

	def receive: Receive = {
		case AuthenticateUser(request) =>
			authentication.login(request).mapTo[Token] pipeTo sender
		case ValidateAuth(request) =>
			authentication.authenticated(request.token).mapTo[Token] pipeTo sender
	}
}
