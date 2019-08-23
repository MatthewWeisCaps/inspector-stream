package core

import org.reactivestreams.Publisher
import org.scalatest.FunSuite
import reactor.core.publisher.{Flux => JFlux}
import reactor.test.StepVerifier
import test.StepVerifierExt._

class SwitchOnNextTest extends FunSuite {

  test("switchOnNext") {

    val letters = Flux.just("a", "b", "c")
    val numbers = Flux.just(1, 2, 3)

    val both = Flux.just(letters, numbers).asInstanceOf[Publisher[_ <: Publisher[Any]]]
    val flux = Flux.switchOnNext(both)

    StepVerifier.create(flux)
      .expectSubscription()
      .expectNext("a")
      .expectNext("b")
      .expectNext("c")
      .expectNext(1)
      .expectNext(2)
      .expectNext(3)
      .expectComplete()
      .verify()
  }

}
