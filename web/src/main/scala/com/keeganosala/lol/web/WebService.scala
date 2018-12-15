package com.keeganosala.lol.web

import scala.concurrent.Future
import scala.util.{ Failure, Success }

import akka.actor.ActorSystem
import akka.actor.Props
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.RawHeader
import akka.util.Timeout
import akka.pattern.ask

import com.keeganosala._

import lol.core.config.LolConfig

import lol.core.query._

import QueryService._

import lol.core.writer._

import WriteToDbService._

import lol.core.util._

import lol.core.util.auth._

import AuthenticationService._

import lol.market.computations._

import ComputationsService._

case class Token (
	token:String
)

trait WebService extends LolJsonProtocol 
	with CorsHandler 
	with AuthenticationLogic {
  
  implicit def system: ActorSystem

  private val computationsService    = system.actorOf(Props[ComputationsService])

  private val writeToDbService  	 = system.actorOf(Props[WriteToDbService])

  private val dbQueryService  	     = system.actorOf(Props[QueryService])

  private val authenticationService  = system.actorOf(Props[AuthenticationService])

  implicit val timeout             	 = Timeout(LolConfig.httpRequestTimeout)

  lazy val route = pathPrefix("lol"){ 
  	corsHandler (concat(
  		path("data/get"){
	  		get {
		  		authenticated{claims=>
			  	rejectEmptyResponse {
			  	onComplete((computationsService ? GetGraph))  { 
			  		case Success(data) => 
			  			complete(data.asInstanceOf[Map[String,Map[String,String]]])
			  		case Failure(e) => 
			  			complete(StatusCodes.BadRequest)
						}
					}
				}
			}
  		},
  		path("data/compute"){
	  		get {
		  		authenticated{claims=>
			  	rejectEmptyResponse {
			  	onComplete((computationsService ? GetVolatility))  { 
			  		case Success(data) => 
			  			complete(data.asInstanceOf[Map[String,String]])
			  		case Failure(e) => 
			  			complete(StatusCodes.BadRequest)
						}
					}

				}
			}
  		},
  		path("auth"){
			concat(post {
				entity(as[AuthenticateUserServiceRequest]) { 
				  loginreq =>
				  onComplete((authenticationService ? loginreq).mapTo[String]) {
				    case Success(token) => 
				    respondWithHeader(RawHeader("Access-Token", token)) {
				      complete(new Token(token = token))
				    }
				    case Failure(exception) => 
				      complete(StatusCodes.BadRequest)
				  }
				}
			},
			get {
			    authenticated { claims =>
			      complete(s"authentication still valid!")
			    }
			  })
  		},
  		path("users"){
	      concat(
	        pathEnd {
	          concat(
	            get {
	              authenticated { claims => 
	                val users: Future[UsersFetchQueryServiceResponse] =
	                  (dbQueryService ? UsersFetchQueryServiceRequest).mapTo[UsersFetchQueryServiceResponse]
	                complete(users)
	              }
	            },
	            post {
	              entity(as[RegisterUser]) { user =>
	                val userCreated =
	                  (writeToDbService ? user)
	                  complete((StatusCodes.Created))
	              }
	            }
	          )
	        },
	        path(Segment) { id =>
	          concat(
	            get {
	              authenticated { claims => 
	                val maybeUser: Future[SingleUserFetchQueryServiceResponse] =
	                  (dbQueryService ? SingleUserFetchQueryServiceRequest(id)
	              ).mapTo[SingleUserFetchQueryServiceResponse]
	                rejectEmptyResponse {
	                  complete(maybeUser)
	                }
	              }
	            },
	            delete {
	              authenticated { claims =>
	                val userDeleted =
	                  (writeToDbService ? DeleteUser(id))
	                  complete((StatusCodes.OK))
	              }
	            }
	          )
	        }
	      )
  		}))
  	}
}
