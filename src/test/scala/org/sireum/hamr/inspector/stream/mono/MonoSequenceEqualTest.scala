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
