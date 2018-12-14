package com.keeganosala.lol
package web

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.io.StdIn

import akka.actor.{ ActorRef, ActorSystem, Props }
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.stream.ActorMaterializer

import com.keeganosala._

import lol.core.config.LolConfig

class Server extends App {

  private val applicationName = "Lol"

  implicit val actorSystem    = ActorSystem(s"$applicationName-system")

  implicit val materializer: ActorMaterializer = ActorMaterializer()

  lazy val log = Logging(actorSystem, classOf[Server])

  val bindingFuture = Http().bindAndHandle(
                  new WebService {
                    override def actorRefFactory = actorSystem
                  }.route,
                  LolConfig.apiInterface, 
                  LolConfig.apiPort
                )
  log.info(s"Server online at http://localhost:8080\nPress Enter to stop...")

  StdIn.readLine()
  bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => actorSystem.terminate())

  Await.result(actorSystem.whenTerminated, Duration.Inf)
}
