package server

//#user-registry-actor
import akka.actor.{ ActorRef, Actor, ActorLogging, Props, ActorSystem}
import akka.pattern.ask
import models._
import models._
import akka.util.Timeout
import java.io.IOException
import java.io.FileNotFoundException


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
//TODO: fix timeout bug while accessing the UserRegistryActor exiting with an internal server error

class UserRegistryActor extends Actor with ActorLogging {
  
  import UserRegistryActor._

  import BasicDataBase._

  var users = Set.empty[User]

  var user = User 

  def receive: Receive = {
    case GetUsers =>
      BasicDataBase.getUsers()
      // sender() ! ActionPerformed(s"Users from table users retrieved.")
    case CreateUser(user) =>
      BasicDataBase.addUser(user)
      // sender() ! ActionPerformed(s"User ${user.name} created.")
    case GetUser(name) =>
      BasicDataBase.getUser(name)
    case DeleteUser(name) =>
      BasicDataBase.deleteUser(name)
      // sender() ! ActionPerformed(s"User ${name} deleted.")
    }
  }
//#user-registry-actor
