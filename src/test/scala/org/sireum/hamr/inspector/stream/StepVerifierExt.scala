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
