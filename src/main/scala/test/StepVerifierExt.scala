package test

import java.time.{Duration => JDuration}
import java.util.function.{Supplier => JSupplier}

import org.reactivestreams.Publisher
import reactor.test.{StepVerifier, StepVerifierOptions}

import scala.concurrent.duration.FiniteDuration
import scala.language.implicitConversions

object StepVerifierExt {

  // pimp my library pattern

//  class StepVerifier_Ext(stepVerifier: StepVerifier) {
//    def withVirtualTime[T](scenarioSupplier: () => Publisher[T]): StepVerifier.FirstStep[T] = {
//      StepVerifier.withVirtualTime(scalaToJavaSupplier(scenarioSupplier))
//    }
//  }

//  object StepVerifierVirtualTime extends AnyVal {
//    def withVirtualTime[T](scenarioSupplier: () => Publisher[T]): StepVerifier.FirstStep[T] = {
//      StepVerifier.withVirtualTime[T](scalaToJavaSupplier(scenarioSupplier))
//    }
//  }
//
//  implicit def stepVerifier_Ext(stepVerifier: StepVerifier.type): StepVerifierVirtualTime.type = StepVerifierVirtualTime




  class StepVerifierStep_Ext[T](stepVerifierStep: StepVerifier.Step[T]) {
    def expectNoEvent(duration: FiniteDuration): reactor.test.StepVerifier.Step[T] = {
      stepVerifierStep.expectNoEvent(scalaToJavaDuration(duration))
    }
  }

//  def scenario(name: String): StepVerifierOptions = StepVerifierOptions.create().scenarioName(name)

  implicit def stepVerifierStep_Ext[T](stepVerifierStep: StepVerifier.Step[T]): StepVerifierStep_Ext[T] =
    new StepVerifierStep_Ext[T](stepVerifierStep)

  implicit def scalaToJavaDuration(finiteDuration: FiniteDuration): JDuration = JDuration.ofNanos(finiteDuration.toNanos)
  implicit def scalaToJavaSupplier[T](supplier: () => T): JSupplier[T] = () => supplier()

}