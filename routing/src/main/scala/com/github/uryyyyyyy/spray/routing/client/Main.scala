package com.github.uryyyyyyy.spray.routing.client

import scala.util.{Failure, Success}
import akka.actor.ActorSystem
import akka.event.Logging

object Main extends ConnectionLevelApiDemo
with HostLevelApiDemo
with RequestLevelApiDemo {

	def main(args: Array[String]) {

		// we always need an ActorSystem to host our application in
		implicit val system = ActorSystem("simple-example")
		import system.dispatcher // execution context for future transformations below
		val log = Logging(system, getClass)

		// the spray-can client-side API has three levels (from lowest to highest):
		// 1. the connection-level API
		// 2. the host-level API
		// 3. the request-level API
		//
		// this example demonstrates all three APIs by retrieving the server-version
		// of http://spray.io in three different ways

		val host = "spray.io"

		val result = for {
			result1 <- demoConnectionLevelApi(host)
			result2 <- demoHostLevelApi(host)
			result3 <- demoRequestLevelApi(host)
		} yield Set(result1, result2, result3)

		result onComplete {
			case Success(res) => log.info("{} is running {}", host, res mkString ", ")
			case Failure(error) => log.warning("Error: {}", error)
		}
		result onComplete { _ => system.shutdown() }
	}
}


trait RequestLevelApiDemo {
	import scala.concurrent.Future
	import scala.concurrent.duration._
	import akka.actor.ActorSystem
	import akka.io.IO
	import akka.pattern.ask
	import akka.util.Timeout
	import spray.http._
	import spray.can.Http
	import HttpMethods._

	private implicit val timeout: Timeout = 5.seconds

	// The request-level API is the highest-level way to access the spray-can client-side infrastructure.
	// All you have to do is to send an HttpRequest instance to `IO(Http)` and wait for the response.
	// The spray-can HTTP infrastructure looks at the URI (or the Host header if the URI is not absolute)
	// to figure out which host to send the request to. It then sets up a HostConnector for that host
	// (if it doesn't exist yet) and forwards it the request.
	def demoRequestLevelApi(host: String)(implicit system: ActorSystem): Future[ProductVersion] = {
		import system.dispatcher // execution context for future transformation below
		for {
			response <- IO(Http).ask(HttpRequest(GET, Uri(s"http://$host/"))).mapTo[HttpResponse]
		} yield {
			system.log.info("Request-Level API: received {} response with {} bytes",
				response.status, response.entity.data.length)
			response.header[HttpHeaders.Server].get.products.head
		}
	}

}

trait HostLevelApiDemo {
	import scala.concurrent.Future
	import scala.concurrent.duration._
	import akka.actor.ActorSystem
	import akka.util.Timeout
	import akka.pattern.ask
	import akka.io.IO
	import spray.can.Http
	import spray.http._
	import HttpMethods._

	private implicit val timeout: Timeout = 5.seconds

	// With the host-level API you ask the spray-can HTTP infrastructure to setup an
	// "HttpHostConnector" for you, which is an entity that manages a pool of connection to
	// one particular host. Once set up you can send the host connector HttpRequest instances,
	// which it will schedule across a connection from its pool (according to its configuration)
	// and deliver the responses back to the request sender

	def demoHostLevelApi(host: String)(implicit system: ActorSystem): Future[ProductVersion] = {
		import system.dispatcher // execution context for future transformations below
		for {
			Http.HostConnectorInfo(hostConnector, _) <- IO(Http) ? Http.HostConnectorSetup(host, port = 80)
			response <- hostConnector.ask(HttpRequest(GET, "/")).mapTo[HttpResponse]
		} yield {
			system.log.info("Host-Level API: received {} response with {} bytes",
				response.status, response.entity.data.length)
			response.header[HttpHeaders.Server].get.products.head
		}
	}

}

trait ConnectionLevelApiDemo {
	import scala.concurrent.Future
	import scala.concurrent.duration._
	import akka.io.IO
	import akka.util.Timeout
	import akka.pattern.ask
	import akka.actor._
	import spray.can.Http
	import spray.http._
	import HttpMethods._

	private implicit val timeout: Timeout = 5.seconds

	def demoConnectionLevelApi(host: String)(implicit system: ActorSystem): Future[ProductVersion] = {
		val actor = system.actorOf(Props(new MyRequestActor(host)), name = "my-request-actor")
		val future = actor ? HttpRequest(GET, "/")
		future.mapTo[ProductVersion]
	}

	// The connection-level API is the lowest-level way to access the spray-can client-side infrastructure.
	// With it you are in charge of establishing, using, and tearing down the HTTP connections yourself.
	// The benefit is that you have complete control over when connections are being established and torn down
	// as well as how requests are scheduled onto them.

	// Actor that manages the lifecycle of a single HTTP connection for a single request
	class MyRequestActor(host: String) extends Actor with ActorLogging {
		import context.system

		def receive: Receive = {
			case request: HttpRequest =>
				// start by establishing a new HTTP connection
				IO(Http) ! Http.Connect(host, port = 80)
				context.become(connecting(sender, request))
		}

		def connecting(commander: ActorRef, request: HttpRequest): Receive = {
			case _: Http.Connected =>
				// once connected, we can send the request across the connection
				sender ! request
				context.become(waitingForResponse(commander))

			case Http.CommandFailed(Http.Connect(address, _, _, _, _)) =>
				log.warning("Could not connect to {}", address)
				commander ! Status.Failure(new RuntimeException("Connection error"))
				context.stop(self)
		}

		def waitingForResponse(commander: ActorRef): Receive = {
			case response@ HttpResponse(status, entity, _, _) =>
				log.info("Connection-Level API: received {} response with {} bytes", status, entity.data.length)
				sender ! Http.Close
				context.become(waitingForClose(commander, response))

			case ev@(Http.SendFailed(_) | Timedout(_))=>
				log.warning("Received {}", ev)
				commander ! Status.Failure(new RuntimeException("Request error"))
				context.stop(self)
		}

		def waitingForClose(commander: ActorRef, response: HttpResponse): Receive = {
			case ev: Http.ConnectionClosed =>
				log.debug("Connection closed ({})", ev)
				commander ! Status.Success(response.header[HttpHeaders.Server].get.products.head)
				context.stop(self)

			case Http.CommandFailed(Http.Close) =>
				log.warning("Could not close connection")
				commander ! Status.Failure(new RuntimeException("Connection close error"))
				context.stop(self)
		}
	}
}