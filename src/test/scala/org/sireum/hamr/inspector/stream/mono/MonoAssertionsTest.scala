package org.sireum.hamr.inspector.stream.mono

import org.scalatest.funsuite.AnyFunSuite
import org.sireum.hamr.inspector.stream.Mono
import reactor.test.StepVerifier

class MonoAssertionsTest extends AnyFunSuite {

  test("mono assertAlways test passing") {
    val mono: Mono[String] = Mono.just("abc").assertAlways(_.matches("[a-z]*"))

    StepVerifier.create(mono)
      .expectSubscription()
      .expectNext("abc")
      .expectComplete()
      .verify()
  }

  test("mono assertNever test failing") {
    val mono: Mono[String] = Mono.just("abc").map(_.toUpperCase).assertAlways(_.matches("[a-z]*"))

    StepVerifier.create(mono)
      .expectSubscription()
      .expectError()
      .verify()
  }

  test("mono chaining AssertAlways test with first assertion failing") {
    val mono: Mono[String] = Mono.just("abc")
      .assertAlways((s: String) => s.matches("[A-Z]*"), "assertion 1")
      .map(_.toUpperCase())
      .assertAlways((s: String) => s.matches("[A-Z]*"), "assertion 2")

    StepVerifier.create(mono)
      .expectSubscription()
      .expectErrorMessage("assertion 1 ==> expected: <true> but was: <false>")
      .verify()
  }

  test("mono chaining AssertAlways test with second assertion failing") {
    val mono: Mono[String] = Mono.just("abc")
      .assertAlways((s: String) => s.matches("[a-z]*"), "assertion 1")
      .map(_.toUpperCase)
      .assertAlways((s: String) => s.matches("[a-z]*"), "assertion 2")

    StepVerifier.create(mono)
      .expectSubscription()
      .expectErrorMessage("assertion 2 ==> expected: <true> but was: <false>")
      .verify()
  }

  test("mono assertEmpty passing") {
    val mono: Mono[String] = Mono.empty().assertEmpty()

    StepVerifier.create(mono)
      .expectSubscription()
      .expectComplete()
      .verify()
  }

  test("mono assertEmpty failing") {
    val mono: Mono[String] = Mono.just("abc").assertEmpty()

    StepVerifier.create(mono)
      .expectSubscription()
      .expectError()
      .verify()
  }

  test("mono assertNotEmpty passing") {
    val mono: Mono[String] = Mono.just("abc").assertNotEmpty()

    StepVerifier.create(mono)
      .expectSubscription()
      .expectNext("abc")
      .expectComplete()
      .verify()
  }

  test("mono assertNotEmpty failing") {
    val mono: Mono[String] = Mono.just("abc").ignoreElement().assertNotEmpty()

    StepVerifier.create(mono)
      .expectSubscription()
      .expectError()
      .verify()
  }


}
