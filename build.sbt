enablePlugins(JavaAppPackaging,JavaServerAppPackaging,sbtdocker.DockerPlugin,DockerComposePlugin)

lazy val sharedSettings = Seq(
  organization := "com.keeganosala",
  version      := "0.1.1",
  scalaVersion := "2.12.6",
  resolvers    ++= Seq(
    "Typesafe repository releases" at "http://repo.typesafe.com/typesafe/releases/",
    "Confluent Maven Repository" at "http://packages.confluent.io/maven/"
  ),
  scalacOptions ++= Seq(
    "-deprecation",
    "-feature",
    "-unchecked"
  )
)

val akkaVersion      = "2.5.16"
val akkaHttpVersion  = "10.1.5"
val scalaTestVersion = "3.0.5"
lazy val lol = (project in file("."))
  .aggregate(core, market, web)

lazy val core = (project in file("core")).
  settings(
    sharedSettings,
    libraryDependencies ++= Seq(
      "com.github.mauricio" %% "postgresql-async"     % "0.2.21",
      "ch.qos.logback"      %  "logback-classic"      % "1.1.3" % Runtime,
      "de.heikoseeberger"   %% "akka-http-circe"      % "1.17.0",
      "com.jason-goodwin"   %% "authentikat-jwt"      % "0.4.5",
      "io.circe"            %% "circe-generic"        % "0.8.0",
      "org.scalacheck"      %% "scalacheck"           % "1.13.5",
      "org.json4s"          %% "json4s-core"          % scalaTestVersion,
      "org.json4s"          %% "json4s-jackson"       % scalaTestVersion,
      "org.json4s"          %% "json4s-native"        % scalaTestVersion,
      "org.scalatest"       %% "scalatest"            % scalaTestVersion,
      "de.heikoseeberger"   %% "akka-http-json4s"     % "1.11.0",
      "com.typesafe.akka"   %% "akka-http-spray-json" % "10.1.5",
      "com.typesafe.akka"   %% "akka-stream"          % akkaVersion,
    )
  )

lazy val market = (project in file("market")).
  settings(
    sharedSettings,
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor"   % akkaVersion,
      "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
    )
  ).dependsOn(core)

lazy val web = (project in file("web")).
  settings(
    sharedSettings,
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http"         % "10.1.5",
      "com.typesafe.akka" %% "akka-http-testkit" % "10.1.5" % Test,
    )
  ).dependsOn(core, market)


import sbtdocker._

lazy val dockerSettings = Seq(
    // things the docker file generation depends on are listed here
    dockerfile in docker := {
        // any vals to be declared here
        new sbtdocker.mutable.Dockerfile {
            // <<docker commands>>
        }
    }
) 

dockerImageCreationTask := docker.value

dockerImageCreationTask := (publishLocal in Docker).value

cancelable in Global := true
