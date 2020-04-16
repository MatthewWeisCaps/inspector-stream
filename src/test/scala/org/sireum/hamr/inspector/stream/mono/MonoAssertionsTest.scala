/*
 * Copyright (c) 2020, Matthew Weis, Kansas State University
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.sireum.hamr.inspector.stream.mono

import org.scalatest.funsuite.AnyFunSuite
import org.sireum.hamr.inspector.stream.Mono
import reactor.test.StepVerifier

class MonoAssertionsTest extends AnyFunSuite {

  test("mono assertAlways test passing") {
    val mono: Mono[String] = Mono.just("abc").assertTrue(_.matches("[a-z]*"))

    StepVerifier.create(mono)
      .expectSubscription()
      .expectNext("abc")
      .expectComplete()
      .verify()
  }

  test("mono assertNever test failing") {
    val mono: Mono[String] = Mono.just("abc").map(_.toUpperCase).assertTrue(_.matches("[a-z]*"))

    StepVerifier.create(mono)
      .expectSubscription()
      .expectError()
      .verify()
  }

  test("mono chaining AssertAlways test with first assertion failing") {
    val mono: Mono[String] = Mono.just("abc")
      .assertTrue((s: String) => s.matches("[A-Z]*"), "assertion 1")
      .map(_.toUpperCase())
      .assertTrue((s: String) => s.matches("[A-Z]*"), "assertion 2")

    StepVerifier.create(mono)
      .expectSubscription()
      .expectErrorMessage("assertion 1 ==> expected: <true> but was: <false>")
      .verify()
  }

  test("mono chaining AssertAlways test with second assertion failing") {
    val mono: Mono[String] = Mono.just("abc")
      .assertTrue((s: String) => s.matches("[a-z]*"), "assertion 1")
      .map(_.toUpperCase)
      .assertTrue((s: String) => s.matches("[a-z]*"), "assertion 2")

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
