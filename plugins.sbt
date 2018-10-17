libraryDependencies ++= Seq(

"com.typesafe.akka" %% "akka-http-spray-json" % "10.1.5",

"com.typesafe.akka" %% "akka-actor" % "2.5.16",

"com.typesafe.akka" %% "akka-testkit" % "2.5.16" % Test,

"com.typesafe.akka" %% "akka-stream" % "2.5.16",

"com.typesafe.akka" %% "akka-stream-testkit" % "2.5.16" % Test,

"com.typesafe.akka" %% "akka-http" % "10.1.5",

"com.typesafe.akka" %% "akka-http-testkit" % "10.1.5" % Test,

"com.github.mauricio" %% "postgresql-async" % "0.2.21",

"ch.qos.logback" % "logback-classic" % "1.1.3" % Runtime,

"de.heikoseeberger" %% "akka-http-circe"     % "1.17.0",

"com.jason-goodwin" %% "authentikat-jwt"     % "0.4.5",

"io.circe"          %% "circe-generic"       % "0.8.0",

"org.scalacheck"    %% "scalacheck"          % "1.13.5",

"org.json4s" %% "json4s-core" % "3.5.0",

"org.json4s" %% "json4s-jackson" % "3.5.0",

"org.json4s" %% "json4s-native" % "3.5.0",

"org.scalatest" %% "scalatest" % "3.0.3",

"de.heikoseeberger" %% "akka-http-json4s" % "1.11.0"

)