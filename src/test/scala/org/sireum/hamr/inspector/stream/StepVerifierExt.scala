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

package org.sireum.hamr.inspector.stream

import java.time.{Duration => JDuration}
import java.util.function.{Supplier => JSupplier}

import reactor.test.StepVerifier

import scala.concurrent.duration.FiniteDuration
import scala.language.implicitConversions

object StepVerifierExt {

  class StepVerifierStep_Ext[T](stepVerifierStep: StepVerifier.Step[T]) {
    def expectNoEvent(duration: FiniteDuration): reactor.test.StepVerifier.Step[T] = {
      stepVerifierStep.expectNoEvent(scalaToJavaDuration(duration))
    }
  }

  implicit def stepVerifierStep_Ext[T](stepVerifierStep: StepVerifier.Step[T]): StepVerifierStep_Ext[T] =
    new StepVerifierStep_Ext[T](stepVerifierStep)

  implicit def scalaToJavaDuration(finiteDuration: FiniteDuration): JDuration = JDuration.ofNanos(finiteDuration.toNanos)
  implicit def scalaToJavaSupplier[T](supplier: () => T): JSupplier[T] = () => supplier()

}
