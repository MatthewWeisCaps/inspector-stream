package org.sireum.hamr.inspector.stream.mono

import org.scalatest.funsuite.AnyFunSuite
import org.sireum.hamr.inspector.stream.Mono
import reactor.test.StepVerifier

class MonoZipWithTest extends AnyFunSuite {

  test("mono zipWith") {

    val a = Mono.just("a")
    val b = Mono.just("b")

    StepVerifier.create(a.zipWith(b))
      .expectSubscription()
      .expectNext(("a", "b"))
      .expectComplete()
      .verify()
  }

  test("mono zipWith combinator") {

    val a = Mono.just("a")
    val b = Mono.just("b")

    val concatFn = (s1: String, s2: String) => s1 ++ s2

    StepVerifier.create(a.zipWith(b, concatFn))
      .expectSubscription()
      .expectNext("ab")
      .expectComplete()
      .verify()
  }

  
}
