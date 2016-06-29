package com.github.uryyyyyyy.spray.helloWorld

import spray.routing.Directives._

class MyService2 {
  val myRoute =
    path("service2" / LongNumber) { id =>
      get {
        complete("OK " + id)
      }
    }
}