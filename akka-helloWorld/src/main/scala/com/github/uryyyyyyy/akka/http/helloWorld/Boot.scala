package com.github.uryyyyyyy.akka.http.helloWorld

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer

object Boot extends App {
  implicit val system = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()

  val myService = new MyService()

  Http().bindAndHandle(myService.route, "localhost", 8080)
}