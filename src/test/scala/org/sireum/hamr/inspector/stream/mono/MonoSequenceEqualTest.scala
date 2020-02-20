package org.sireum.hamr.inspector.stream.mono

import org.scalatest.funsuite.AnyFunSuite
import org.sireum.hamr.inspector.stream.{Flux, Mono}
import reactor.test.StepVerifier

class MonoSequenceEqualTest extends AnyFunSuite {

  test("mono_sequenceEqual_True") {

    val mono = Mono.sequenceEqual(Mono.just("apple"), Mono.just("apple"))

    StepVerifier.create(mono)
      .expectSubscription()
      .expectNext(true)
      .expectComplete()
      .verify()
  }

  test("mono_sequenceEqual_False") {

    val mono = Mono.sequenceEqual(Mono.just("apple"), Mono.just("banana"))

    StepVerifier.create(mono)
      .expectSubscription()
      .expectNext(false)
      .expectComplete()
      .verify()
  }

  test("mono_sequenceEqual_Transforming") {

    val seq1 = Flux.just(2, 4, 6, 8, 10)
    val seq2 = Flux.range(1, 5).map(_ * 2)

    val mono = Mono.sequenceEqual(seq1, seq2)

    StepVerifier.create(mono)
      .expectSubscription()
      .expectNext(true)
      .expectComplete()
      .verify()
  }

}
