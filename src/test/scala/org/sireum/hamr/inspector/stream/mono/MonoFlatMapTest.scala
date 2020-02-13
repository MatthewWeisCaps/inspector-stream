package org.sireum.hamr.inspector.stream.mono

import org.scalatest.funsuite.AnyFunSuite
import org.sireum.hamr.inspector.stream.Mono
import reactor.test.StepVerifier

class MonoFlatMapTest extends AnyFunSuite {

  test("mono flatMap") {

    val mono = Mono.just("a").flatMap(letter => Mono.just(letter.toUpperCase()))

    StepVerifier.create(mono)
      .expectSubscription()
      .expectNext("A")
      .expectComplete()
      .verify()
  }

  
}
