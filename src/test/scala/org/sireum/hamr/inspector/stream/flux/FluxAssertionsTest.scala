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

package org.sireum.hamr.inspector.stream.flux

import java.util.concurrent.TimeUnit.SECONDS

import org.scalatest.funsuite.AnyFunSuite
import org.sireum.hamr.inspector.stream.Flux
import org.sireum.hamr.inspector.stream.StepVerifierExt._
import reactor.test.StepVerifier

import scala.concurrent.duration.FiniteDuration

class FluxAssertionsTest extends AnyFunSuite {

  test("flux always positive test passing") {
    val flux: Flux[Int] = Flux.range(1, 100)
      .assertTrue(_ > 0)

    StepVerifier.create(flux)
      .expectSubscription()
      .expectNextCount(100)
      .expectComplete()
      .verify()
  }

  test("flux always positive test failing") {
    val flux: Flux[Int] = Flux.range(1, 100)
      .map(_ - 50)
      .assertTrue(_ > 0)

    StepVerifier.create(flux)
      .expectSubscription()
      .expectError()
      .verify()
  }

  test("flux never negative test passing") {
    val flux: Flux[Int] = Flux.range(1, 100)
      .assertFalse(_ < 0)

    StepVerifier.create(flux)
      .expectSubscription()
      .expectNextCount(100)
      .expectComplete()
      .verify()
  }

  test("flux never negative test failing") {
    val flux: Flux[Int] = Flux.range(1, 100)
      .map(_ - 50)
      .assertFalse(_ < 0)

    StepVerifier.create(flux)
      .expectSubscription()
      .expectError()
      .verify()
  }

  test("flux chaining assertions passing") {
    val flux: Flux[Int] = Flux.range(1, 100)
      .assertTrue((n: Int) => n > 0, "assertion 1")
      .map(_ * -1)
      .assertFalse((n: Int) => n > 0, "assertion 2")

    StepVerifier.create(flux)
      .expectSubscription()
      .expectNextCount(100)
      .expectComplete()
      .verify()
  }

  test("flux chaining assertions with first failing") {
    val flux: Flux[Int] = Flux.range(1, 100)
      .assertTrue((n: Int) => n < 0, "assertion 1")
      .map(_ * -1)
      .assertFalse((n: Int) => n > 0, "assertion 2")

    StepVerifier.create(flux)
      .expectSubscription()
      .expectErrorMessage("assertion 1 ==> expected: <true> but was: <false>")
      .verify()
  }

  test("flux chaining assertions with second failing") {
    val flux: Flux[Int] = Flux.range(1, 100)
      .assertTrue((n: Int) => n > 0, "assertion 1")
      .map(_ * -1)
      .assertFalse((n: Int) => n < 0, "assertion 2")

    StepVerifier.create(flux)
      .expectSubscription()
      .expectErrorMessage("assertion 2 ==> expected: <false> but was: <true>")
      .verify()
  }

  test("flux expect error after some pass") {
    val flux: Flux[Int] = Flux.range(1, 100)
      .map(n => 50 - n)
      .assertTrue(_ > 0)

    StepVerifier.create(flux)
      .expectSubscription()
      .expectNext(49, 48, 47, 46, 45)
      .expectNextCount(40)
      .expectNext(4, 3, 2, 1)
      .expectError()
      .verify()
  }

  test("flux assertEmpty passing") {
    val flux: Flux[Int] = Flux.empty().assertEmpty()

    StepVerifier.create(flux)
      .expectSubscription()
      .expectComplete()
      .verify()
  }

  test("flux assertEmpty passing complex 1") {
    val flux: Flux[Int] = Flux.just(1, 2, 3)
      .map(_ + 10)
      .filter(_ < 10).assertEmpty()

    StepVerifier.create(flux)
      .expectSubscription()
      .expectComplete()
      .verify()
  }

  test("flux assertEmpty passing complex 2") {
    val fluxSupplier: () => Flux[Int] = () => Flux.just(1, 2, 3)
      .delaySequence(FiniteDuration(6, SECONDS))
      .take(FiniteDuration(5, SECONDS))
      .assertEmpty()

    StepVerifier.withVirtualTime(fluxSupplier)
      .expectSubscription()
      .expectNoEvent(FiniteDuration(5, SECONDS))
      .thenAwait(FiniteDuration(1, SECONDS))
      .expectComplete()
      .verify()
  }

  test("flux assertEmpty failing") {
    val flux: Flux[Int] = Flux.just(1, 2, 3).assertEmpty()

    StepVerifier.create(flux)
      .expectSubscription()
      .expectError()
      .verify()
  }

  test("flux assertEmpty passing complex") {
    val fluxSupplier: () => Flux[Int] = () => Flux.just(1, 2, 3)
      .delaySequence(FiniteDuration(4, SECONDS))
      .take(FiniteDuration(5, SECONDS))
      .assertEmpty()

    StepVerifier.withVirtualTime(fluxSupplier)
      .expectSubscription()
      .expectNoEvent(FiniteDuration(4, SECONDS))
      .expectError()
      .verify()
  }

  test("flux assertNotEmpty passing") {
    val flux: Flux[Int] = Flux.just(1, 2, 3)
      .assertNotEmpty()

    StepVerifier.create(flux)
      .expectSubscription()
      .expectNext(1, 2, 3)
      .expectComplete()
      .verify()
  }

  test("flux assertNotEmpty failing") {
    val flux: Flux[Int] = Flux.just(1, 2, 3)
      .filter(_ > 10)
      .assertNotEmpty()

    StepVerifier.create(flux)
      .expectSubscription()
      .expectError()
      .verify()
  }

}
