package org.sireum.hamr.inspector.stream.flux

import org.scalatest.funsuite.AnyFunSuite
import org.sireum.hamr.inspector.stream.Flux
import reactor.test.StepVerifier

class FluxJustTest extends AnyFunSuite {

  test("just") {
    StepVerifier.create(Flux.just("apple"))
      .expectSubscription()
      .expectNext("apple")
      .expectComplete()
      .verify()
  }

  test("justVarargs") {
    StepVerifier.create(Flux.just("apple", "banana", "orange"))
      .expectSubscription()
      .expectNext("apple")
      .expectNext("banana")
      .expectNext("orange")
      .expectComplete()
      .verify()
  }

}
