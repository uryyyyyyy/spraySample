package com.github.uryyyyyyy.spray.helloWorld

import org.scalatest.{FunSpec, MustMatchers}
import spray.http.StatusCodes
import spray.routing.HttpService._
import spray.testkit.ScalatestRouteTest

class MyServiceSpec extends FunSpec with MustMatchers with ScalatestRouteTest {

  describe("test") {

    it("test1") {
      val myService2 = new MyService2()
      Get("/service2/3") ~> sealRoute(myService2.myRoute) ~> check {
        status mustBe StatusCodes.OK
        body.contentType.mediaType.value mustBe "text/plain"
        body.contentType.definedCharset.get.value mustBe "UTF-8"
        body.data.asString mustBe "OK 3"
      }
    }

    it("test2") {
      val myService2 = new MyService2()
      Post("/service2/3") ~> sealRoute(myService2.myRoute) ~> check {
        status mustBe StatusCodes.MethodNotAllowed
      }
    }
  }
}