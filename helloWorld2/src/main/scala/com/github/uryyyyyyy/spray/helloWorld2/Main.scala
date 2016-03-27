package com.github.uryyyyyyy.spray.helloWorld2

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http

object Main {

	def main(args: Array[String]): Unit = {
		implicit val system = ActorSystem()

		// the handler actor replies to incoming HttpRequests
		val handler = system.actorOf(Props[DemoService], name = "handler")

		IO(Http) ! Http.Bind(handler, interface = "localhost", port = 8080)
	}

}