package test.mono

import core.Mono
import org.scalatest.funsuite.AnyFunSuite

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
