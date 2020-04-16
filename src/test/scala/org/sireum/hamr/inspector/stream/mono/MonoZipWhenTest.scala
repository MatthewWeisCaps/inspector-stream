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

import java.util.concurrent.TimeUnit

import org.scalatest.funsuite.AnyFunSuite
import org.sireum.hamr.inspector.stream.Mono
import org.sireum.hamr.inspector.stream.StepVerifierExt._
import reactor.test.StepVerifier
import reactor.test.scheduler.VirtualTimeScheduler

import scala.concurrent.duration.Duration

class MonoZipWhenTest extends AnyFunSuite {

  test("mono zipWhen") {

    VirtualTimeScheduler.getOrSet()

    val a = Mono.just("a").delayElement(Duration(1, TimeUnit.SECONDS))
    val b = (s: String) => Mono.just(s.toUpperCase)

    StepVerifier.withVirtualTime(() => a.zipWhen(b))
      .expectSubscription()
      .expectNoEvent(Duration(1, TimeUnit.SECONDS))
      .expectNext(("a", "A"))
      .expectComplete()
      .verify()
  }

  test("mono zipWhen combinator") {

    val a = Mono.just("a").delayElement(Duration(1, TimeUnit.SECONDS))
    val b = (s: String) => Mono.just(s.toUpperCase)

    val concatFn = (s1: String, s2: String) => s1 ++ s2

    StepVerifier.create(a.zipWhen(b, concatFn))
      .expectSubscription()
      .expectNext("aA")
      .expectComplete()
      .verify()
  }

  
}
