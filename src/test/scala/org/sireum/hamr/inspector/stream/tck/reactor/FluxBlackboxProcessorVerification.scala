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

import java.util.concurrent.TimeUnit

import org.sireum.hamr.inspector.stream.Flux
import org.testng.annotations.{AfterTest, BeforeTest, Test}
import reactor.core.scheduler.{Scheduler, Schedulers}

import scala.concurrent.duration.FiniteDuration


/*
  * Port of Reactor's TCK:
  * https://github.com/reactor/reactor-core/blob/master/reactor-core/src/test/java/reactor/core/publisher/tck/FluxBlackboxProcessorVerification.java
  *
  * MUST BE RUN WITH TESTNG.
  */
@Test
final class FluxBlackboxProcessorVerification extends AbstractFluxVerification {

  private var sharedGroup: Scheduler = null

  override def transformFlux(f: Flux[Int]): Flux[Int] = {
    val otherStream = Flux.just("test", "test2", "test3")
    val asyncGroup = Schedulers.newParallel("flux-p-tck", 2)

    val combinator = (t1: Int, t2: String) => t1

    return f.publishOn(sharedGroup)
        .parallel(2)
        .groups()
        .flatMap(stream => stream.publishOn(asyncGroup)
          .doOnNext(monitorThreadUse)
          .scan((prev, next) => next)
          .map(integer => -integer)
          .filter(integer => integer <= 0)
          .map(integer => -integer)
          .bufferTimeout(batch, FiniteDuration(50, TimeUnit.MILLISECONDS))
          .flatMap(Flux.fromIterable)
          .flatMap(i => Flux.zip(Flux.just(i), otherStream, combinator)))
        .publishOn(sharedGroup)
        .doAfterTerminate(() => asyncGroup.dispose())
        .doOnError(throwable => throwable.printStackTrace())
  }

  @BeforeTest
  def beforeEach(): Unit = {
    sharedGroup = Schedulers.newParallel("fluxion-tck", 2)
  }

  @AfterTest
  def afterEach(): Unit = {
    sharedGroup.dispose()
  }

}








