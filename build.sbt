name := """spraySamples"""

version := "1.0"

val akkaV = "2.3.9"
val sprayV = "1.3.3"

lazy val commonSettings = Seq(
  organization := "com.github.uryyyyyyy",
  scalaVersion := "2.11.7",
  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-testkit" % akkaV % "test",
    "io.spray" %% "spray-http" % sprayV,
    "io.spray" %% "spray-can" % sprayV,
    "io.spray" %% "spray-routing-shapeless2" % sprayV,
    "io.spray" %% "spray-testkit" % sprayV % "test",
    "org.scalatest" %% "scalatest" % "3.0.0-M15" % "test",
    "org.scalaz" %% "scalaz-core" % "7.2.4"
  )
)

// spray plugin?
Revolver.settings

lazy val helloWorld = (project in file("helloWorld"))
  .settings(commonSettings: _*)