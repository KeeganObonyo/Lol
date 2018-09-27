package users

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
import users.UserRegistryActor._
import akka.pattern.ask
import akka.util.Timeout

//#user-routes-class
trait UserRoutes extends JsonSupport {
  //#user-routes-class

  // we leave these abstract, since they will be provided by the App
  implicit def system: ActorSystem

  lazy val log = Logging(system, classOf[UserRoutes])

  // other dependencies that UserRoutes use
  def userRegistryActor: ActorRef

  // Required by the `ask` (?) method below
  implicit lazy val timeout = Timeout(5.seconds) // usually we'd obtain the timeout from the system's configuration


  private def ModelTorow(user:UserPost): Array[Any] = {
      Array(
        user.name,
        user.email,
        user.password
      )
    }
  //#all-routes
  //#users-get-post
  //#users-get-delete   
  lazy val userRoutes: Route =
    pathPrefix("users") {
      concat(
        pathEnd {
          concat(
            get {
              val users: Future[Users] =
                (userRegistryActor ? GetUsers).mapTo[Users]
              complete(users)
            },
            post {
              entity(as[UserPost]) { userp =>
                val userCreated =
                  (userRegistryActor ? CreateUser(ModelTorow(userp)))
                onSuccess(userCreated) { performed =>
                  log.info("Created user")
                  complete((StatusCodes.Created))
                }
              }
            }
          )
        },
        //#users-get-post
        //#users-get-delete
        path(Segment) { id =>
          concat(
            get {
              //#retrieve-user-info
              val maybeUser: Future[User] =
                (userRegistryActor ? GetUser(id)).mapTo[User]
              rejectEmptyResponse {
                complete(maybeUser)
              }
              //#retrieve-user-info
            },
            delete {
              //#users-delete-logic
              val userDeleted =
                (userRegistryActor ? DeleteUser(id))
              onSuccess(userDeleted) { performed =>
                log.info("Deleted user")
                complete((StatusCodes.OK))
              }
              //#users-delete-logic
            }
          )
        }
      )
      //#users-get-delete
    }
  //#all-routes
}