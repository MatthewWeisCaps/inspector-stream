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

class FluxZipTest extends AnyFunSuite {

  test("flux zip3 singles") {
    val flux = Flux.zip(Flux.just("a"), Flux.just("b"), Flux.just("c"))

    StepVerifier.create(flux)
      .expectSubscription()
      .expectNext(("a", "b", "c"))
      .expectComplete()
      .verify()
  }

  test("flux zipMap") {
    val flux = Flux.zip(Flux.just("a"), Flux.just("b"), Flux.just("c"))

    StepVerifier.create(flux.map(((a: String, b: String, c: String) => a + b + c).tupled))
      .expectSubscription()
      .expectNext("abc")
      .expectComplete()
      .verify()
  }

  test("flux zip2") {

    val letters = Flux.just("a", "b", "c")
    val numbers = Flux.just(1, 2, 3)

    val flux = Flux.zip(letters, numbers)

    StepVerifier.create(flux)
      .expectSubscription()
      .expectNext(("a", 1))
      .expectNext(("b", 2))
      .expectNext(("c", 3))
      .expectComplete()
      .verify()
  }

  test("flux zip3 with 3 elements") {

    val numbers = Flux.just(1, 2, 3)
    val letters = Flux.just("a", "b", "c")
    val names = Flux.just("Alice", "Bob", "Chris")

    val flux = Flux.zip(numbers, letters, names)

    StepVerifier.create(flux)
      .expectSubscription()
      .expectNext((1, "a", "Alice"))
      .expectNext((2, "b", "Bob"))
      .expectNext((3, "c", "Chris"))
      .expectComplete()
      .verify()
  }

  test("flux zip4") {

    val numbers = Flux.just(1, 2, 3)
    val letters = Flux.just("a", "b", "c")
    val names = Flux.just("Alice", "Bob", "Chris")
    val states = Flux.just("Alaska", "California", "Delaware")

    val flux = Flux.zip(numbers, letters, names, states)

    StepVerifier.create(flux)
      .expectSubscription()
      .expectNext((1, "a", "Alice", "Alaska"))
      .expectNext((2, "b", "Bob", "California"))
      .expectNext((3, "c", "Chris", "Delaware"))
      .expectComplete()
      .verify()
  }

  test("flux zipIterable") {

    val numbers = Flux.just(1, 2, 3) // requires boxing in StepVerifier below!
    val letters = Flux.just("a", "b", "c")
    val names = Flux.just("Alice", "Bob", "Chris")

    val flux = Flux.zip(Seq(numbers, letters, names), (arr: Array[AnyRef]) => (arr(0), arr(1), arr(2)))

    StepVerifier.create(flux)
      .expectSubscription()
      .expectNext((Int.box(1), "a", "Alice"))
      .expectNext((Int.box(2), "b", "Bob"))
      .expectNext((Int.box(3), "c", "Chris"))
      .expectComplete()
      .verify()
  }

  test("flux zipPublisher") {

    val numbers = Flux.just(1, 2, 3) // requires boxing in StepVerifier below!
    val letters = Flux.just("a", "b", "c")
    val names = Flux.just("Alice", "Bob", "Chris")

    val oneFourSeven = Flux.just(1, 4, 7)
    val twoFiveEight = Flux.just(2, 5, 8)
    val threeSixNine = Flux.just(3, 6, 9)

    val flux = Flux.zip(Flux.just(oneFourSeven, twoFiveEight, threeSixNine), ((a: Int, b: Int, c: Int) => a + b + c).asInstanceOf[(Any, Any, Any) => Int])

    StepVerifier.create(flux)
      .expectSubscription()
      .expectNext(6)
      .expectNext(15)
      .expectNext(24)
      .expectComplete()
      .verify()
  }

}
