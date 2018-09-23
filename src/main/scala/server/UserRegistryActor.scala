package server

//#user-registry-actor
import akka.actor.{ ActorRef, Actor, ActorLogging, Props, ActorSystem}
import akka.pattern.ask
import models.BasicDataBaseActor._
import models._
import akka.util.Timeout


//#user-case-classes
final case class User(name: String, age: Int, countryOfResidence: String)

final case class Users(users: Seq[User])


//#user-case-classes

object UserRegistryActor {
  final case class ActionPerformed(description: String)
  final case object GetUsers
  final case class CreateUser(user: User)
  final case class GetUser(name: String)
  final case class DeleteUser(name: String)

  // def props: Props = Props[UserRegistryActor]
}

class UserRegistryActor extends Actor with ActorLogging {
  
  import UserRegistryActor._

  implicit val system  = context.system
 
  val basicDataBaseActor: ActorRef = system.actorOf(Props[BasicDataBaseActor], "basicDataBaseActor")

  var users = Set.empty[User]

  var user = User 

//TODO Add methods to map query results to our User classes and handle exceptions
  def receive: Receive = {
    case GetUsers =>
      val result = (basicDataBaseActor ! Query("SELECT * FROM users"))
    case CreateUser(user) =>
      (basicDataBaseActor ! Write("insert to users (name, age, countryofresidence) values ($1, $2, $3) returning id"))
      sender() ! ActionPerformed(s"User ${user.name} created.")
    case GetUser(name) =>
      (basicDataBaseActor ! Query( "SELECT * FROM users WHERE users.name = $1"))
    case DeleteUser(name) =>
      (basicDataBaseActor ! Write("delete from users where id = $1"))
      sender() ! ActionPerformed(s"User ${name} deleted.")

  }
  }
//#user-registry-actor
