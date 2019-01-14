package com.keeganosala.lol.web

import scala.concurrent.ExecutionContext.Implicits.global
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

import lol.market._

import DataAccessService._

case class Token (
	token:String
)

trait WebService extends LolJsonProtocol 
	with CorsHandler 
	with AuthenticationLogic {
  
  implicit def system: ActorSystem

  val dataAccessService    			 = system.actorOf(Props[DataAccessService])

  private val writeToDbService  	 = system.actorOf(Props[WriteToDbService])

  private val dbQueryService  	     = system.actorOf(Props[QueryService])

  private val authenticationService  = system.actorOf(Props[AuthenticationService])

  implicit val timeout             	 = Timeout(LolConfig.httpRequestTimeout)

  lazy val route = 
  	corsHandler (
  	pathPrefix("lol"){ 
  		concat(
  		path("data" / "get" / Segment) { symbol =>
		  		get {
			  		authenticated{ claims=>
				  	rejectEmptyResponse {
				  	onComplete((dataAccessService ? GraphDataRequest(symbol)).mapTo[GraphDataRequestResponse]) { 
				  		case Success(graphdata) => 
				  			complete(graphdata.data)
				  		case Failure(e) => 
				  			complete(StatusCodes.BadRequest)
						}
					}
				}
			}
  		},
  		path("data" / "compute"/ Segment) { symbol=>
		  		get {
			  		authenticated{ claims=>
				  	rejectEmptyResponse {
				  	onComplete((dataAccessService ? VolatilityAnalysisRequest(symbol)).mapTo[VolatilityAnalysisResponse]) { 
				  		case Success(volatilitycalculation) => 
				  			complete(volatilitycalculation.data)
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
				    respondWithHeader(RawHeader("Authorization", token)) {
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
  		pathPrefix ("users"){
	      concat(
	        pathEnd {
	          concat(
	            get {
	              authenticated { claims => 
	                onComplete((dbQueryService ? UsersFetchQueryServiceRequest).mapTo[UsersFetchQueryServiceResponse])
	                 {
	                	case Success(users)=>
	                  		complete(users)
	                  	case Failure(error)=>
	                  		println(error.toString)
	                  		complete(StatusCodes.BadRequest)
	                	}
	              	}
	            },
	            post {
	              entity(as[RegisterUser]) { user =>
	                onComplete((writeToDbService ? user)) {
	                	case Success(done)=>
	                  		complete(StatusCodes.Created)
	                  	case Failure(error)=>
	                  		println(error.toString)
	                  		complete(StatusCodes.BadRequest)
	                }
	              }
	            }
	          )
	        },
	        path(IntNumber) { id =>
	          concat(
	            get {
	              authenticated { claims => 
	              onComplete((dbQueryService ? SingleUserFetchQueryServiceRequest(id)
	                ).mapTo[SingleUserFetchQueryServiceResponse]) {
				    case Success(user) => 
	                  complete(user)
				    case Failure(error) => 
				      complete(StatusCodes.NotFound)
				  	}
	              }
	            },
	            delete {
	              authenticated { claims =>
	                val userDeleted =
	                  (writeToDbService ? DeleteUser(id))
	                  complete((StatusCodes.OK))
	              	}
	            })
	        })
  		})
  	})
}
