package test.flux

import core.Flux
import org.scalatest.funsuite.AnyFunSuite
import reactor.test.StepVerifier

class FluxFirstTest extends AnyFunSuite {

  test("first_1") {
    val a: Flux[Any] = Flux.just(1, 2, 3)
    val b: Flux[Any] = Flux.just("a", "b", "c")

    StepVerifier.create(Flux.first(a, b))
      .expectSubscription()
      .expectNext(1)
      .expectNext(2)
      .expectNext(3)
      .expectComplete()
      .verify()
  }

  test("first_2") {

    val a: Flux[Any] = Flux.just(1, 2, 3)
    val b: Flux[Any] = Flux.just("a", "b", "c")

    StepVerifier.create(Flux.first(b, a)) // <-- NOTICE is (b, a) not (a, b)
      .expectSubscription()
      .expectNext("a")
      .expectNext("b")
      .expectNext("c")
      .expectComplete()
      .verify()
  }

}
