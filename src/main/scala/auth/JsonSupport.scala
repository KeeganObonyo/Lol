import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

trait JsonSupport extends SprayJsonSupport {
  
  import DefaultJsonProtocol._

  implicit val loginrequestJsonFormat = jsonFormat2(LoginRequest)

  implicit val authconditionJsonFormat = jsonFormat1(AuthCondition) 
  
  implicit val tokenJsonFormat = jsonFormat1(Token)

}