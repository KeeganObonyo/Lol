package users

import users.UserRegistryActor.ActionPerformed

//#json-support
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

trait JsonSupport extends SprayJsonSupport {
  // import the default encoders for primitive types (Int, String, Lists etc)
  
  import DefaultJsonProtocol._

  implicit val userJsonFormat = jsonFormat4(User)

  implicit val userpostJsonFormat = jsonFormat3(UserPost)
  
  implicit val usersJsonFormat = jsonFormat1(Users)

  implicit val actionPerformedJsonFormat = jsonFormat1(ActionPerformed)
}
//#json-support