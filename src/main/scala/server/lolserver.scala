// package server

// //User registration actor
// import akka.actor.{ Actor, ActorLogging, Props }

// final case class User(name: String, age: Int, countryOfResidence: String)
// final case class Users(users: Seq[User])

// object UserRegistryActor {
//   final case class ActionPerformed(description: String)
//   final case object GetUsers
//   final case class CreateUser(user: User)
//   final case class GetUser(name: String)
//   final case class DeleteUser(name: String)

//   // def props: Props = Props[UserRegistryActor]
// }


// class UserRegistryActor extends Actor with ActorLogging {

//   import UserRegistryActor._

//   var users = Set.empty[User]
//   var user = Set.empty[User]

//   def receive: Receive = {
//     case GetUsers =>
//       sender() ! Users(users.toSeq)
//     case CreateUser(user) =>
//       users += user
//       sender() ! ActionPerformed(s"User \${user.name} created.")
//     case GetUser(name) =>
//       sender() ! users.find(_.name == name)
//     case DeleteUser(name) =>
//       users.find(_.name == name) foreach { user => users -= user }
//       sender() ! ActionPerformed(s"User \${name} deleted.")
//   }
// }

// //server logic
// import scala.concurrent.{ Await, ExecutionContext, Future }
// import scala.concurrent.duration.Duration
// import scala.util.{ Failure, Success }

// import akka.actor.{ ActorRef, ActorSystem }
// import akka.http.scaladsl.Http
// import akka.http.scaladsl.server.Route
// import akka.stream.ActorMaterializer
// import akka.http.scaladsl.server.Directives._
// import akka.http.scaladsl.model.StatusCodes
// import akka.event.Logging
// import akka.http.scaladsl.marshalling.ToResponseMarshallable
// import akka.pattern.ask

// object QuickstartServer extends App {

// // set up ActorSystem and other dependencies here
// implicit val system: ActorSystem = ActorSystem("helloAkkaHttpServer")
// implicit val materializer: ActorMaterializer = ActorMaterializer()
// implicit val executionContext: ExecutionContext = system.dispatcher

// val serverBinding: Future[Http.ServerBinding] = Http().bindAndHandle(routes, "localhost", 8080)

// val userRegistryActor: ActorRef = system.actorOf(Props[UserRegistryActor], name = "userRegistryActor")

// val log = Logging(system, getClass)

// // from the UserRoutes trait

// lazy val userRoutes: Route =
//   pathPrefix("users") {
//     concat(
//       pathEnd {
//         concat(
//           get {
//             val users: Future[Users] =
//               (userRegistryActor ? GetUsers).mapTo[Users]
//             complete("users")
//           },
//           post {
//             entity(as[User]) { user =>
//               val userCreated: Future[ActionPerformed] =
//                 (userRegistryActor ? CreateUser(user)).mapTo[ActionPerformed]
//               onSuccess(userCreated) { performed =>
//                 log.info("Created user [{}]: {}", user.name, performed)
//                 complete((StatusCodes.Created, performed))
//               }
//             }
//           }
//         )
//       },
//       path(Segment) { name =>
//         concat(
//           get {
//             val maybeUser: Future[Option[User]] =
//               (userRegistryActor ? GetUser(name)).mapTo[Option[User]]
//             rejectEmptyResponse {
//               complete("maybeUser")
//             }
//           },
//           delete {
//             val userDeleted: Future[ActionPerformed] =
//               (userRegistryActor ? DeleteUser(name)).mapTo[ActionPerformed]
//             onSuccess(userDeleted) { performed =>
//               log.info("Deleted user [{}]: {}", name, performed)
//               complete((StatusCodes.OK, performed))
//             }
//           }
//         )
//       }
//     )
//   }

// lazy val routes: Route = userRoutes

// serverBinding.onComplete {
//   case Success(bound) =>
//     println(s"Server online at http://\${bound.localAddress.getHostString}:\${bound.localAddress.getPort}/")
//   case Failure(e) =>
//     Console.err.println(s"Server could not start!")
//     e.printStackTrace()
//     system.terminate()
// }

// Await.result(system.whenTerminated, Duration.Inf)

// }

// import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
// import spray.json.DefaultJsonProtocol

// trait JsonSupport extends SprayJsonSupport {
//   // import the default encoders for primitive types (Int, String, Lists etc)
//   import DefaultJsonProtocol._

//   implicit val userJsonFormat = jsonFormat3(User)
//   implicit val usersJsonFormat = jsonFormat1(Users)

//   implicit val actionPerformedJsonFormat = jsonFormat1(ActionPerformed)
// }
