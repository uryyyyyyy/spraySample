package com.github.uryyyyyyy.spray.helloWorld

import org.scalatest.{FunSpec, MustMatchers}
import spray.http.StatusCodes
import spray.routing.HttpService._
import spray.testkit.ScalatestRouteTest

class MyService3Spec extends FunSpec with MustMatchers with ScalatestRouteTest {

  describe("test") {
    val myService3 = new MyService3()
    it("test1") {
      Get("/service3_3/3") ~> sealRoute(myService3.myRoute) ~> check {
        status mustBe StatusCodes.OK
        body.contentType.mediaType.value mustBe "text/plain"
        body.contentType.definedCharset.get.value mustBe "UTF-8"
        body.data.asString mustBe "OK 3"
      }
    }

    it("test2") {
      Get("/service3_3/3a") ~> sealRoute(myService3.myRoute) ~> check {
        status mustBe StatusCodes.InternalServerError
        body.contentType.mediaType.value mustBe "text/plain"
        body.contentType.definedCharset.get.value mustBe "UTF-8"
        body.data.asString mustBe "cannot cast to number: 3a"
      }
    }
  }
}