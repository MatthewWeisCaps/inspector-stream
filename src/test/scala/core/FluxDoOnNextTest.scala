package core

import org.reactivestreams.{Subscriber, Subscription}
import org.scalatest.FunSuite
import reactor.test.StepVerifier
import org.scalatest.Assertions._

import scala.concurrent.duration._

class FluxDoOnNextTest extends FunSuite {

  test("doOnNextAssignment") {
    var sideEffect = "banana"

    Flux.just("apple")
      .doOnNext(s => sideEffect = s)
      .subscribe(_ => Unit, it => throw it)

    assert(sideEffect == "apple")
  }

  test("doOnNextAddition") {
    var sideEffect = 0

    Flux.just(1, 2, 3)
      .doOnNext(n => sideEffect += n)
      .subscribe()

    assert(sideEffect == 1 + 2 + 3)
  }

}
