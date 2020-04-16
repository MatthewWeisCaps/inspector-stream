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

//import scala.language.implicitConversions

class FluxSwitchOnFirstTest extends AnyFunSuite {

  test("flux switchOnFirst") {

    val flux = Flux.just(3, 5, 1, 9, 6, 2, 0, 4)
      .switchOnFirst((s, flux) => {
        if (s.hasValue) {
          flux.filter(n => n >= s.get())
        } else {
          flux
        }
      })

    StepVerifier.create(flux)
      .expectSubscription()
      .expectNext(3)
      .expectNext(5)
      // skip 1 because 1 is not greater than or equal to 3
      .expectNext(9)
      .expectNext(6)
      // skip 2 because 2 is not greater than or equal to 3
      // skip 0 because 0 is not greater than or equal to 3
      .expectNext(4)
      .expectComplete()
      .verify()
  }

}
