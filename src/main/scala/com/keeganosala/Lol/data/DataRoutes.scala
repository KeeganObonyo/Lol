package com.keeganosala.Lol
package data

import scala.util.{Failure,Success}
import scala.concurrent.ExecutionContext.Implicits.global

import akka.actor.{ ActorRef, Actor, ActorLogging, Props, ActorSystem}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.{ Directive1, Route,Directives }
import akka.http.scaladsl.server.Directives._



trait DataRoutes extends Data {

  def data = get {
  	getData()
    complete(StatusCodes.OK)
  }

  lazy val dataRoutes: Route = path("data"){
  	data
  }
}