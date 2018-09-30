package com.keeganosala.Lol
package users

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
final case class User(id: Int, name: String, email: String)

final case class UserInstance(id: Int, name: String, email: String, password: String)

final case class UserPost(name: String, email: String, password: String)

final case class Users(users: List[User])


object UserRegistryActor {
  final case class ActionPerformed(description: String)
  final case object GetUsers
  final case class CreateUser(user:Array[Any])
  final case class GetUser(name: String)
  final case class DeleteUser(name: String)

}

class UserRegistryActor extends Actor with ActorLogging {
  
  import UserRegistryActor._

  val databaseinstance = DbTransactions()

  def receive: Receive = {
    case GetUsers =>
      databaseinstance.getUsers.mapTo[Users] pipeTo sender
    case CreateUser(user) =>
      sender ! databaseinstance.addUser(user)
    case GetUser(id) =>
      databaseinstance.getUser(id).mapTo[User] pipeTo sender
    case DeleteUser(id) =>
      sender ! databaseinstance.deleteUser(id)
    }
  }
