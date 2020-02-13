package org.sireum.hamr.inspector.stream.flux

import org.reactivestreams.Publisher
import org.scalatest.funsuite.AnyFunSuite
import org.sireum.hamr.inspector.stream.Flux
import reactor.test.StepVerifier

class FluxSwitchOnNextTest extends AnyFunSuite {

  test("flux switchOnNext") {

    val letters = Flux.just("a", "b", "c")
    val numbers = Flux.just(1, 2, 3)

    val both = Flux.just(letters, numbers).asInstanceOf[Publisher[_ <: Publisher[Any]]]
    val flux = Flux.switchOnNext(both)

    StepVerifier.create(flux)
      .expectSubscription()
      .expectNext(Seq("a"):_*)
      .expectNext(Seq("b"):_*)
      .expectNext(Seq("c"):_*)
      .expectNext(Seq(1):_*)
      .expectNext(Seq(2):_*)
      .expectNext(Seq(3):_*)
      .expectComplete()
      .verify()
  }

  test("flux switchOnNext contingent") {

    val letters = Flux.just("a").concatWith(Flux.just("b", "c"))
    val numbers = Flux.just(1, 2, 3)

    val both = Flux.just(letters, numbers).asInstanceOf[Publisher[_ <: Publisher[Any]]]
    val flux = Flux.switchOnNext(both)

    StepVerifier.create(flux)
      .expectSubscription()
      .expectNext(Seq("a"):_*)
      .expectNext(Seq("b"):_*)
      .expectNext(Seq("c"):_*)
      .expectNext(Seq(1):_*)
      .expectNext(Seq(2):_*)
      .expectNext(Seq(3):_*)
      .expectComplete()
      .verify()
  }

}
