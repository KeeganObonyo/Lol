package com.keeganosala.Lol
package auth

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

trait JsonSupport extends SprayJsonSupport {
  
  import DefaultJsonProtocol._

  implicit val loginRequestJsonFormat = jsonFormat2(LoginRequest)
  
}