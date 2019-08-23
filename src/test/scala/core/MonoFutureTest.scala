package core

import org.scalatest.{Assertions, FunSuite}
import reactor.test.StepVerifier
import test.StepVerifierExt._

class MonoFutureTest extends FunSuite {

  test("mono future") {
    val future = Mono.just("apple").toFuture

    future.value match {
      case Some(v) => assert(v.get == "apple")
      case None => fail
    }
  }
  
}
