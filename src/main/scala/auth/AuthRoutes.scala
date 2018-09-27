package auth

import akka.actor.{ ActorRef, ActorSystem }
import akka.event.Logging

import scala.concurrent.duration._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.delete
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.MethodDirectives.post
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.server.directives.PathDirectives.path

import scala.concurrent.Future
import auth.authenticationActor._
import akka.pattern.ask
import akka.util.Timeout

trait AuthRoutes {

  implicit val system: system.context

  lazy val log = Logging(system, classOf[UserRoutes])

  val authenticationActor: ActorRef = system.actorOf(Props[AuthenticationActor], "authenticationActor")

  implicit lazy val timeout = Timeout(5.seconds)

	lazy val authRoutes: Route =

	    pathPrefix("auth") {
	        pathEnd {
	          concat(
	            get {
	              val condition: Future[AuthCondition] =
	                (authenticationActor ? ValidateAuth).mapTo[AuthCondition]
	                  log.info("Validated")
	              complete(StatusCodes.OK,condition)
	            },
	            post {
	              entity(as[LoginRequest]) { authreq =>
	                val token =
	                  (authenticationActor ? AuthenticateUser(authreq)).mapTo[Token]
	                onSuccess(token) { performed =>
	                  log.info("Authenticated")
	                  complete(StatusCodes.OK,token)
	                }
	              }
	            }
	          )
	        }
	    }
}
