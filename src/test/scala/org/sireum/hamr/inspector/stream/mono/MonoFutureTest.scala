package org.sireum.hamr.inspector.stream.mono

import org.scalatest.funsuite.AnyFunSuite
import org.sireum.hamr.inspector.stream.Mono

import scala.util.{Failure, Success, Try}

class MonoFutureTest extends AnyFunSuite {

  test("mono future success") {
    val future = Mono.just("apple").toFuture

    future.value match {
      case Some(v) => assert(v.get == "apple")
      case None => fail
    }
  }


  test("mono exception toFuture should return Some(error)") {
    val exception = new RuntimeException("intentional error")
    val future = Mono.error[String](exception).toFuture

    future.value match {
      case Some(v) => v match {
        case Success(s) => fail // we shouldn't have success
        case Failure(e) => assert(e == exception)
      }
      case None => fail
    }
  }
  
}
