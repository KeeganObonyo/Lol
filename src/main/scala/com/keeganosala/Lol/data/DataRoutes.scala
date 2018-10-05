package com.keeganosala.Lol
package data

import scala.util.{Failure,Success}
import scala.concurrent.ExecutionContext.Implicits.global
import java.util.concurrent.TimeUnit
import scala.concurrent.duration._

import akka.util.Timeout
import akka.actor.{ ActorRef, Actor, ActorLogging, Props, ActorSystem}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.{ Directive1, Route,Directives }
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask

import data.DataAccessActor._



trait DataRoutes {

  implicit def system: ActorSystem

  def dataAccessActor: ActorRef

  implicit lazy val timedelta = Timeout(5.seconds)

  def getdata = get {
  	onComplete((dataAccessActor ? GetData)) { 
  		case Success(data) => 
  			// println(data)
  			complete(StatusCodes.OK)
  		case Failure(e) => 
  			// println(e)
  			complete(StatusCodes.OK)
		}
	}

  lazy val dataRoutes: Route = path("data"){ 
  	getdata 
  }

}