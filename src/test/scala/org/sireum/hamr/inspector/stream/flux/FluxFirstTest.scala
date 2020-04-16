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

import org.scalatest.funsuite.AnyFunSuite
import org.sireum.hamr.inspector.stream.Flux
import reactor.test.StepVerifier

class FluxFirstTest extends AnyFunSuite {

  test("first_1") {
    val a: Flux[Any] = Flux.just(1, 2, 3)
    val b: Flux[Any] = Flux.just("a", "b", "c")

    StepVerifier.create(Flux.first(a, b))
      .expectSubscription()
      .expectNext(Seq(1):_*)
      .expectNext(Seq(2):_*)
      .expectNext(Seq(3):_*)
      .expectComplete()
      .verify()
  }

  test("first_2") {

    val a: Flux[Any] = Flux.just(1, 2, 3)
    val b: Flux[Any] = Flux.just("a", "b", "c")

    StepVerifier.create(Flux.first(b, a)) // <-- NOTICE is (b, a) not (a, b)
      .expectSubscription()
      .expectNext(Seq("a"):_*)
      .expectNext(Seq("b"):_*)
      .expectNext(Seq("c"):_*)
      .expectComplete()
      .verify()
  }

}
