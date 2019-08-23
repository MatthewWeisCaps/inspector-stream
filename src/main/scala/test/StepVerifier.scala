//package test
//
//import java.time.{Duration => JDuration}
//import java.util.function.{Supplier => JSupplier}
//
//import org.reactivestreams.Publisher
//import reactor.test.scheduler.VirtualTimeScheduler
//import reactor.test.{StepVerifierOptions, StepVerifier => JStepVerifier}
//
//import scala.concurrent.duration.Duration.Infinite
//import scala.concurrent.duration.{Duration, FiniteDuration}
//import scala.language.implicitConversions
//
//object StepVerifier {
//
//  // pimp my library pattern
//
//  class StepVerifierStep_Ext[T](stepVerifierStep: JStepVerifier.Step[T]) {
//    def expectNoEvent(duration: FiniteDuration): JStepVerifier.Step[T] = {
//      stepVerifierStep.expectNoEvent(scalaToJavaDuration(duration))
//    }
//  }
//
//  implicit def stepVerifierStep_Ext[T](stepVerifierStep: JStepVerifier.Step[T]): StepVerifierStep_Ext[T] =
//    new StepVerifierStep_Ext[T](stepVerifierStep)
//
//  def scalaToJavaDuration(finiteDuration: FiniteDuration): JDuration = JDuration.ofNanos(finiteDuration.toNanos)
//  def scalaToJavaSupplier[T](supplier: () => T): JSupplier[T] = () => supplier()
//
//  // static delegation
//
//  def setDefaultTimeout(timeout: Duration): Unit = timeout match {
//    case _: Infinite => JStepVerifier.resetDefaultTimeout() // resets value to Infinite
//    case duration: FiniteDuration => JStepVerifier.setDefaultTimeout(scalaToJavaDuration(duration))
//  }
//
//  def resetDefaultTimeout(): Unit = JStepVerifier.resetDefaultTimeout()
//
//  def create[T](publisher: Publisher[T]): JStepVerifier.FirstStep[T] = JStepVerifier.create(publisher)
//
//  def create[T](publisher: Publisher[T], n: Long): JStepVerifier.FirstStep[T] = JStepVerifier.create(publisher, n)
//
//  def create[T](publisher: Publisher[T], options: StepVerifierOptions): JStepVerifier.FirstStep[T] =
//    JStepVerifier.create(publisher, options)
//
//  def withVirtualTime[T](scenarioSupplier: () => Publisher[T]): JStepVerifier.FirstStep[T] =
//    JStepVerifier.withVirtualTime(scalaToJavaSupplier(scenarioSupplier))
//
//  def withVirtualTime[T](scenarioSupplier: () => Publisher[T], n: Long): JStepVerifier.FirstStep[T] =
//    JStepVerifier.withVirtualTime(scalaToJavaSupplier(scenarioSupplier), n)
//
//  def withVirtualTime[T](scenarioSupplier: () => Publisher[T], vtsLookup: () => VirtualTimeScheduler, n: Long): JStepVerifier.FirstStep[T] =
//    JStepVerifier.withVirtualTime(scalaToJavaSupplier(scenarioSupplier), scalaToJavaSupplier(vtsLookup), n)
//
//  def withVirtualTime[T](scenarioSupplier: () => Publisher[T], options: StepVerifierOptions): JStepVerifier.FirstStep[T] =
//    JStepVerifier.withVirtualTime(scalaToJavaSupplier(scenarioSupplier), options)
//
//}
//
//trait StepVerifier {
//
//  def delegate: JStepVerifier
//
//}
