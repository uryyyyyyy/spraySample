package com.github.uryyyyyyy.spray.routing.router

import akka.actor.ActorSystem
import spray.routing.SimpleRoutingApp

object Main extends SimpleRoutingApp {

	def main(args: Array[String]) {
		implicit val system = ActorSystem("my-system")

		startServer(interface = "localhost", port = 8080) {
			get { // GETのリクエストで
				path("ping") { // /pingへのリクエストで
					complete("PONG") // PONGをレスポンスする
				}
			}
		}
	}
}