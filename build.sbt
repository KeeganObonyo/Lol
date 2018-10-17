name := "LolDB"

organization := "AT"

// version := "0.1"

scalaVersion := "2.11.6"

scalacOptions ++= Seq("-unchecked", "-deprecation")

enablePlugins(JavaAppPackaging,JavaServerAppPackaging,sbtdocker.DockerPlugin)

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

mainClass in Compile := Some("com.keeganosala.Lol.server.LolServer")