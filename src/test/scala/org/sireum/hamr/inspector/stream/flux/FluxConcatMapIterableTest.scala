package org.sireum.hamr.inspector.stream.flux

import org.scalatest.funsuite.AnyFunSuite
import org.sireum.hamr.inspector.stream.Flux
import reactor.test.StepVerifier

class FluxConcatMapIterableTest extends AnyFunSuite {

  test("flux concatMapIterable") {
    val flux = Flux.just(1, 2, 3).concatMapIterable(Seq("a", "b", "c").take)

    StepVerifier.create(flux)
      .expectSubscription()
      .expectNext("a") // 1 -> a
      .expectNext("a") // 2 -> a, b
      .expectNext("b")
      .expectNext("a") // 3 -> a, b, c
      .expectNext("b")
      .expectNext("c")
      .expectComplete()
      .verify()
  }

}
