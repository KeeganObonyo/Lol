package server

//#user-registry-actor
import akka.actor.{ ActorRef, Actor, ActorLogging, Props, ActorSystem}
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import akka.pattern.ask
import models._
import akka.util.Timeout
import java.io.IOException
import java.io.FileNotFoundException
import akka.pattern.pipe

//#user-case-classes
final case class User(name: String, age: Int, countryOfResidence: String)

final case class Users(users: Seq[Any])


object UserRegistryActor {
  final case class ActionPerformed(description: String)
  final case object GetUsers
  final case class CreateUser(user:Array[Any])
  final case class GetUser(name: String)
  final case class DeleteUser(name: String)

}
//TODO: fix timeout bug while accessing the UserRegistryActor exiting with an internal server error

class UserRegistryActor extends Actor with ActorLogging {
  
  import UserRegistryActor._

  val databaseinstance = BasicDataBase()

  def receive: Receive = {
    case GetUsers =>
      databaseinstance.getUsers.mapTo[Users] pipeTo sender
    case CreateUser(user) =>
      sender ! databaseinstance.addUser(user)
    case GetUser(name) =>
      try {
        databaseinstance.getUser(name).mapTo[User] pipeTo sender
      } catch {
        case _:Throwable=>Nil
      }
    case DeleteUser(name) =>
      sender ! databaseinstance.deleteUser(name)
    }
  }
//#user-registry-actor
