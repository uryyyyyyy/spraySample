package com.github.uryyyyyyy.akka.http.helloWorld

import java.util.concurrent.ConcurrentHashMap

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives
import akka.stream.ActorMaterializer

import scala.collection.convert.decorateAsScala._

class MyService {

  implicit val system = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()

  val piyoMap = new ConcurrentHashMap[Int, Map[String, List[String]]]().asScala

  import Directives._
  val route = path("hoge") {
    (get | post) {
      complete("OK")
    }
  } ~ pathPrefix("piyo" / ".+".r) { str =>
    pathEnd {
      sys.error("bomb!!")
    } ~ path(IntNumber) { num =>
      get {
        val piyoOpt = piyoMap.get(num)
        complete(s"${str}, ${num}, ${piyoOpt}")
      } ~ post {
        formFieldMultiMap { form =>
          piyoMap.put(num, form.toMap)
          complete(s"${str}, ${num}, ${form}")
        }
      }
    }
  }
}