package com.keeganosala.Lol
package auth

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol
import users._


trait JsonSupport extends SprayJsonSupport {
  
  import DefaultJsonProtocol._

  implicit val loginRequestJsonFormat = jsonFormat2(LoginRequest)
  
  implicit val userinstanceJsonFormat = jsonFormat4(UserInstance)

  implicit val tokenJsonFormat = jsonFormat1(Token)

}

