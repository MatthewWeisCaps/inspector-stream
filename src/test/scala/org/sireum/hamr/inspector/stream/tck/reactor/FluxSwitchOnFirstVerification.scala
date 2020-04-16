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

package org.sireum.hamr.inspector.stream.tck.reactor

import org.reactivestreams.Publisher
import org.reactivestreams.tck.{PublisherVerification, TestEnvironment}
import org.sireum.hamr.inspector.stream.Flux
import org.testng.annotations.Test


/**
  * Port of Reactor's TCK:
  * https://github.com/reactor/reactor-core/blob/master/reactor-core/src/test/java/reactor/core/publisher/tck/FluxSwitchOnFirstVerification.java
  *
  * MUST BE RUN WITH TESTNG.
  */
@Test
class FluxSwitchOnFirstVerification extends PublisherVerification[Int](new TestEnvironment()) {

  override def createPublisher(elements: Long): Publisher[Int] = {
    Flux.range(0, if (Int.MaxValue < elements) Int.MaxValue else elements.toInt)
      .switchOnFirst((first, innerFlux) => innerFlux)
  }

  override def createFailedPublisher(): Publisher[Int] = {
    Flux.error[Int](new RuntimeException())
      .switchOnFirst((first, innerFlux) => innerFlux)
  }

}








