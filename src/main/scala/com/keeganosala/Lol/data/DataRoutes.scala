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

import data.ComputationsActor._



trait DataRoutes extends AutoMarshalling{

  implicit def system: ActorSystem

  def computationsActor: ActorRef

  implicit lazy val timedelta = Timeout(5.seconds)

  	def getdata = get {
	  	rejectEmptyResponse {
	  	onComplete((computationsActor ? GetGraph))  { 
	  		case Success(data) => 
	  			complete(data.asInstanceOf[Map[String,Map[String,String]]])
	  		case Failure(e) => 
	  			println(e)
	  			complete(StatusCodes.BadRequest)
				}
			}
	}


	def compute = get {
	  	rejectEmptyResponse {
	  	onComplete((computationsActor ? GetVolatility))  { 
	  		case Success(data) => 
	  			complete(data.asInstanceOf[Map[String,String]])
	  		case Failure(e) => 
	  			complete(StatusCodes.BadRequest)
				}
			}
	}

  lazy val dataRoutes: Route = pathPrefix("data"){ 
  	concat(
  		path("get"){
  		getdata 
  	},
  		path("compute"){
  		compute
  		})
  	}

}