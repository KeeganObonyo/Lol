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

//#main-class
object LolServer extends App with UserRoutes {

  // set up ActorSystem and other dependencies here
  //#main-class
  //#server-bootstrapping
  implicit val system: ActorSystem = ActorSystem("LolHttpServer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher


  val userRegistryActor: ActorRef = system.actorOf(Props[UserRegistryActor], "userRegistryActor")


  lazy val homeRoute: Route =
      path("") {
        println("/")

        get {
          complete((StatusCodes.OK,"Home"))
        }
      }

  lazy val routes: Route = concat(userRoutes,homeRoute)

  //#http-server
  val bindingFuture = Http().bindAndHandle(routes, "localhost", 8000)

  println(s"Server online at http://localhost:8000/\nPress Enter to stop...")

  StdIn.readLine() // let it run until user presses return
  bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done

  Await.result(system.whenTerminated, Duration.Inf)
  //#http-server
}
