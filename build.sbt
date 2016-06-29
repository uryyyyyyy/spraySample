name := """spraySamples"""

version := "1.0"

lazy val commonSettings = Seq(
  organization := "com.github.uryyyyyyy",
  scalaVersion := "2.11.7",
  libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest" % "3.0.0-M15" % "test",
    "org.scalaz" %% "scalaz-core" % "7.2.4"
  )
)

// spray plugin?
Revolver.settings

lazy val helloWorld = (project in file("helloWorld"))
  .settings(commonSettings: _*)
  .settings(
    name := "helloWorld",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % "2.3.9",
      "com.typesafe.akka" %% "akka-testkit" % "2.3.9" % "test",
      "io.spray" %% "spray-http" % "1.3.3",
      "io.spray" %% "spray-can" % "1.3.3",
      "io.spray" %% "spray-routing-shapeless2" % "1.3.3",
      "io.spray" %% "spray-testkit" % "1.3.3" % "test"
    )
  )

lazy val akkaHttpHelloWorld = (project in file("akka-helloWorld"))
  .settings(commonSettings: _*)
  .settings(
    name := "akka-helloWorld",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http-core" % "2.4.7",
      "com.typesafe.akka" %% "akka-http-testkit" % "2.4.7",
      "com.typesafe.akka" %% "akka-http-spray-json-experimental" % "2.4.7",
      "org.scalatest" %% "scalatest" % "3.0.0-M15" % "test",
      "org.scalaz" %% "scalaz-core" % "7.2.4"
    )
  )