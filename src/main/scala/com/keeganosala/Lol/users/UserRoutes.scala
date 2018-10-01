package com.keeganosala.Lol
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

trait UserRoutes extends JsonSupport {

  implicit def system: ActorSystem

  lazy val log = Logging(system, classOf[UserRoutes])

  def userRegistryActor: ActorRef

  implicit lazy val timeout = Timeout(5.seconds)


  private def ModelTorow(user:UserPost): Array[Any] = {
      Array(
        user.name,
        user.email,
        user.password
      )
    }
    
  val userRoutes: Route =
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
        path(Segment) { id =>
          concat(
            get {
              val maybeUser: Future[User] =
                (userRegistryActor ? GetUser(id)).mapTo[User]
              rejectEmptyResponse {
                complete(maybeUser)
              }
            },
            delete {
              val userDeleted =
                (userRegistryActor ? DeleteUser(id))
              onSuccess(userDeleted) { performed =>
                log.info("Deleted user")
                complete((StatusCodes.OK))
              }
            }
          )
        }
      )
    }
}
