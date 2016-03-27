name := """spraySamples"""

version := "1.0"

val akkaV = "2.4.2"
val sprayV = "1.3.3"

lazy val commonSettings = Seq(
	organization := "com.github.uryyyyyyy",
	scalaVersion := "2.11.7",
	libraryDependencies ++= Seq(
		"io.spray" %% "spray-http" % sprayV,
		"io.spray"            %%  "spray-can"     % sprayV,
		"io.spray"            %%  "spray-routing" % sprayV,
		"io.spray"            %%  "spray-testkit" % sprayV  % "test",
		"com.typesafe.akka"   %%  "akka-actor"    % akkaV,
		"com.typesafe.akka"   %%  "akka-testkit"  % akkaV   % "test",
		"org.scalatest" %% "scalatest" % "3.0.0-M15" % "test",
		"org.twitter4j" % "twitter4j-core" % "4.0.4"
	)
)

// spray plugin?
Revolver.settings

lazy val helloWorld = (project in file("helloWorld")).
		settings(commonSettings: _*)

lazy val helloWorld2 = (project in file("helloWorld2")).
		settings(commonSettings: _*)

lazy val routing = (project in file("routing")).
		settings(commonSettings: _*)