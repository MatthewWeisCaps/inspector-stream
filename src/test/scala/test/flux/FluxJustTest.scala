package test.flux

import core.Flux
import org.scalatest.FunSuite
import reactor.test.StepVerifier

class FluxJustTest extends FunSuite {

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
