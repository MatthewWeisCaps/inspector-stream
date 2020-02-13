package org.sireum.hamr.inspector.stream.flux

import org.scalatest.funsuite.AnyFunSuite
import org.sireum.hamr.inspector.stream.Flux
import reactor.test.StepVerifier

class FluxFirstTest extends AnyFunSuite {

  test("first_1") {
    val a: Flux[Any] = Flux.just(1, 2, 3)
    val b: Flux[Any] = Flux.just("a", "b", "c")

    StepVerifier.create(Flux.first(a, b))
      .expectSubscription()
      .expectNext(Seq(1):_*)
      .expectNext(Seq(2):_*)
      .expectNext(Seq(3):_*)
      .expectComplete()
      .verify()
  }

  test("first_2") {

    val a: Flux[Any] = Flux.just(1, 2, 3)
    val b: Flux[Any] = Flux.just("a", "b", "c")

    StepVerifier.create(Flux.first(b, a)) // <-- NOTICE is (b, a) not (a, b)
      .expectSubscription()
      .expectNext(Seq("a"):_*)
      .expectNext(Seq("b"):_*)
      .expectNext(Seq("c"):_*)
      .expectComplete()
      .verify()
  }

}
