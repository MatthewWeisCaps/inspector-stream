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
import reactor.test.scheduler.VirtualTimeScheduler

import scala.concurrent.duration.Duration

class FluxRepeatTest extends AnyFunSuite {

  test("flux repeat repeats elements") {
    val flux: Flux[String] = Flux.just("a", "b", "c").repeat()

    StepVerifier.create(flux.take(6))
      .expectSubscription()
      .expectNext("a", "b", "c")
      .expectNext("a", "b", "c")
      .expectComplete()
      .verify()
  }

  test("flux repeat respects time delays") {

    val scheduler = VirtualTimeScheduler.getOrSet()

    val flux: Flux[String] = Flux.just("a", "b", "c").delaySequence(Duration(1, SECONDS)).repeat()

    StepVerifier.withVirtualTime(() => flux.take(6))
      .expectSubscription()
      .expectNoEvent(Duration(1, SECONDS))
      .expectNext("a", "b", "c")
      .expectNoEvent(Duration(1, SECONDS))
      .expectNext("a", "b", "c")
      .expectComplete()
      .verify()
  }

}
