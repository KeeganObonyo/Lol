package com.keeganosala.Lol
package users

import users.UserRegistryActor.ActionPerformed

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

trait JsonSupport extends SprayJsonSupport {
  
  import DefaultJsonProtocol._

  implicit val userJsonFormat = jsonFormat3(User)

  implicit val userpostJsonFormat = jsonFormat3(UserPost) 
  
  implicit val usersJsonFormat = jsonFormat1(Users)

  implicit val actionPerformedJsonFormat = jsonFormat1(ActionPerformed)
}
