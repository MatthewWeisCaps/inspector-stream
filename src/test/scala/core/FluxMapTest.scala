package core

import org.scalatest.FunSuite
import reactor.test.StepVerifier
import test.StepVerifierExt._

class FluxMapTest extends FunSuite {

  test("map") {
    StepVerifier.create(Flux.just(99).map(_ + 1))
      .expectSubscription()
      .expectNext(100)
      .expectComplete()
      .verify()
  }

  test("justVarargs") {
    StepVerifier.create(Flux.just(1, 2, 3).map(_ * 2))
      .expectSubscription()
      .expectNext(2)
      .expectNext(4)
      .expectNext(6)
      .expectComplete()
      .verify()
  }

  test("first") {

    val flux = Flux.first(Flux.just(1), Flux.just(2))

    StepVerifier.create(flux)
      .expectSubscription()
      .expectNext(1)
      .expectComplete()
      .verify()
  }

//  test("firstIterable") {
//
//    val flux = Flux.first(Seq(Flux.just(1), Flux.just(2)))
//
//    StepVerifier.create(flux)
//      .expectSubscription()
//      .expectNext(1)
//      .expectComplete()
//      .verify()
//  }
  
}
