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
import akka.event.Logging


import data.ComputationsActor._

import server._
import auth._


trait DataRoutes extends AutoMarshalling with CORSHandler{

  implicit def system: ActorSystem

  lazy val logg = Logging(system, classOf[DataRoutes])

  def computationsActor: ActorRef

  implicit val timedelta = Timeout(5.seconds)

  val authlogicinst = AuthLogic()


  	def getdata = corsHandler (get {
  		authlogicinst.authenticated{claims=>
	  	rejectEmptyResponse {
	  	onComplete((computationsActor ? GetGraph))  { 
	  		case Success(data) => 
	  			logg.info("SUCCESS")
	  			complete(data.asInstanceOf[Map[String,Map[String,String]]])
	  		case Failure(e) => 
	  			logg.info(e.toString)
	  			complete(StatusCodes.BadRequest)
				}
			}
		}
	})


	def compute =corsHandler ( get {
  		authlogicinst.authenticated{claims=>
	  	rejectEmptyResponse {
	  	onComplete((computationsActor ? GetVolatility))  { 
	  		case Success(data) => 
	  			logg.info("SUCCESS")
	  			complete(data.asInstanceOf[Map[String,String]])
	  		case Failure(e) => 
	  			logg.info(e.toString)
	  			complete(StatusCodes.BadRequest)
				}
			}

		}
	})

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