package com.keeganosala.Lol
package server

//#quick-start-server
import scala.concurrent.Await
import scala.concurrent.duration.Duration

import akka.actor.{ ActorRef, ActorSystem, Props }
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import scala.io.StdIn
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes

import models._
import users._
import auth._
import data._

object LolServer extends App with UserRoutes with AuthRoutes with DataRoutes with CORSHandler {

  implicit val system: ActorSystem = ActorSystem("LolHttpServer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher


  val userRegistryActor: ActorRef = system.actorOf(Props[UserRegistryActor], "userRegistryActor")

  val userInstanceActor: ActorRef = system.actorOf(Props[UserInstanceActor], "userInstanceActor")

  val computationsActor: ActorRef = system.actorOf(Props[ComputationsActor], "computationsActor")


  lazy val homeRoute: Route =
      path("") {
        println("/")

        get {
          complete((StatusCodes.OK,"Home"))
        }
      }

  lazy val routes: Route = concat(userRoutes,homeRoute,authRoutes,dataRoutes)
 

  val bindingFuture = Http().bindAndHandle(routes, "0.0.0.0", 8080)

  println(s"Server online at http://localhost:8080\nPress Enter to stop...")

  StdIn.readLine()
  bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())

  Await.result(system.whenTerminated, Duration.Inf)
}
