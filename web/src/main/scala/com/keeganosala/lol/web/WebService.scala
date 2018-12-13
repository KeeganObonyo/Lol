package com.keeganosala.lol.web

import scala.util.{ Failure, Success }

import akka.actor.ActorRefFactory
import akka.actor.Props
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.RawHeader
import akka.util.Timeout
import akka.pattern.ask

import com.keeganosala._

import lol.core._

import config.LolConfig

import query._

import QueryService._

import writer._

import RegistryUserDbService._

import auth._

import AuthenticationService._

import lol.market._

import DataAccessService._

import lol.web.marshalling._

import lol.util._

trait WebService extends LolJsonProtocol {
  
  private val dataAccessService      = actorRefFactory.actorOf(Props[DataAccessService])

  private val userRegistryService  	 = actorRefFactory.actorOf(Props[RegistryUserDbService])

  private val dbQueryService  	     = actorRefFactory.actorOf(Props[QueryService])

  private val authenticationService  = actorRefFactory.actorOf(Props[AuthenticationService])

  implicit val timeout             	 = Timeout(LolConfig.httpRequestTimeout)

  lazy val route = pathPrefix("lol"){ 
  	corsHandler (concat(
  		path("data/get"){
	  		get {
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
			}
  		},
  		path("data/compute"){
	  		get {
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
	              authlogicinstance.authenticated { claims => 
	                val users: Future[Users] =
	                  (dbQueryService ? GetUsers).mapTo[Users]
	                complete(users)
	              }
	            },
	            post {
	              entity(as[RegisterUser]) { user =>
	                val userCreated =
	                  (userRegistryService ? user)
	                onSuccess(userCreated) { performed =>
	                  complete((StatusCodes.Created))
	                }
	              }
	            }
	          )
	        },
	        path(Segment) { id =>
	          concat(
	            get {
	              authlogicinstance.authenticated { claims => 
	                val maybeUser: Future[User] =
	                  (dbQueryService ? GetUser(id)).mapTo[User]
	                rejectEmptyResponse {
	                  complete(maybeUser)
	                }
	              }
	            },
	            delete {
	              authlogicinstance.authenticated { claims =>
	                val userDeleted =
	                  (userRegistryService ? DeleteUser(id))
	                onSuccess(userDeleted) { performed =>
	                  complete((StatusCodes.OK))
	                }
	              }
	            }
	          )
	        }
	      )
  		}))
  	}
}
