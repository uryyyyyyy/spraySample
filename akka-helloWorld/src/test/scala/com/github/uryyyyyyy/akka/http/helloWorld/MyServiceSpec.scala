package com.github.uryyyyyyy.akka.http.helloWorld

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{FunSpec, MustMatchers}
import akka.http.scaladsl.server._
import Directives._

class MyServiceSpec extends FunSpec with MustMatchers with ScalatestRouteTest {

  describe("test") {

    val smallRoute =
      get {
        pathSingleSlash {
          complete {
            "Captain on the bridge!"
          }
        } ~
          path("ping") {
            complete("PONG!")
          }
      }

    it("return a greeting for GET requests to the root path") {
      Get() ~> smallRoute ~> check {
        responseAs[String] mustBe "Captain on the bridge!"
      }
    }

    it("return a 'PONG!' response for GET requests to /ping") {
      Get("/ping") ~> smallRoute ~> check {
        responseAs[String] mustBe "PONG!"
      }
    }

    it("leave GET requests to other paths unhandled") {
      Get("/kermit") ~> smallRoute ~> check {
        handled mustBe false
      }
    }

    it("return a MethodNotAllowed error for PUT requests to the root path") {
      Put() ~> Route.seal(smallRoute) ~> check {
        status === StatusCodes.MethodNotAllowed
        responseAs[String] mustBe "HTTP method not allowed, supported methods: GET"
      }
    }

  }
}