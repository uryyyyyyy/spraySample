package com.github.uryyyyyyy.spray.helloWorld

import spray.http.RequestProcessingException
import spray.http.StatusCodes._
import spray.routing.Directives._
import spray.routing._

import scalaz.{-\/, \/, \/-}

class MyService3 {
  val myRoute =
    path("service3_1" / Rest) { str =>
      get {
        MyDirectives.strToLong(str) { num =>
          MyDirectives.longToStr(num) { str2 =>
            complete("OK " + str2)
          }
        }
      }
    } ~
      path("service3_2" / Rest) { str =>
        get {
          val d = for {
            num <- MyDirectives.strToLong(str)
            str2 <- MyDirectives.longToStr(num)
          } yield str2
          d { str2 =>
            complete("OK " + str2)
          }
        }
      } ~
      path("service3_3" / Rest) { str =>
        get {
          val either = for {
            num <- MyServices.strToLong(str)
          } yield MyServices.longToStr(num)
          either match {
            case -\/(e) => RESPONSE.failWith_(str, e)
            case \/-(str2) => complete("OK " + str2)
          }
        }
      }

}

object MyDirectives {
  def strToLong(str: String): Directive1[Long] = {
    try {
      val result = str.toLong
      provide(result)
    } catch {
      case e: NumberFormatException => RESPONSE.failWith_(str, e)
    }
  }

  def longToStr(long: Long): Directive1[String] = {
    val result = long.toString
    provide(result)
  }
}

object MyServices {
  def strToLong(str: String): \/[Exception, Long] = {
    try {
      val result = str.toLong
      \/-(result)
    } catch {
      case e: NumberFormatException => -\/(e)
    }
  }

  def longToStr(long: Long): String = {
    val result = long.toString
    result
  }
}

object RESPONSE {
  def failWith_(str: String, e: Exception): StandardRoute = {
    e.printStackTrace()
    complete(InternalServerError, s"cannot cast to number: $str")
  }
}