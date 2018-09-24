libraryDependencies ++= Seq(

"com.typesafe.akka" %% "akka-http-spray-json" % "10.1.5",

"com.typesafe.akka" %% "akka-actor" % "2.5.16",

"com.typesafe.akka" %% "akka-testkit" % "2.5.16" % Test,

"com.typesafe.akka" %% "akka-stream" % "2.5.16",

"com.typesafe.akka" %% "akka-stream-testkit" % "2.5.16" % Test,

"com.typesafe.akka" %% "akka-http" % "10.1.5",

"com.typesafe.akka" %% "akka-http-testkit" % "10.1.5" % Test,

"com.github.mauricio" %% "postgresql-async" % "0.2.21",

"ch.qos.logback" % "logback-classic" % "1.1.3" % Runtime

// "com.h2database" % "h2" % "1.2.137",

// "org.squeryl" % "squeryl_2.10" % "0.9.5-6"

)

