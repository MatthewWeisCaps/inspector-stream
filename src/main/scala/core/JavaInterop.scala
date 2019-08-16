package core

import reactor.core.publisher.{Flux => JFlux}
import java.lang.{Iterable => JIterable}
import java.time.{Duration => JDuration}
import java.lang.{Runnable => JRunnable}
import java.util.function.{Consumer => JConsumer}
import java.util.function.{Function => JFunction}
import java.util.function.{BiFunction => JBiFunction}

import org.reactivestreams.Publisher

import scala.collection.JavaConverters.asJavaIterable
import scala.concurrent.duration.FiniteDuration

object JavaInterop {

  def wrap[T](jFlux: JFlux[T]): Flux[T] = new Flux[T](jFlux)

//  def asJavaDuration(finiteDuration: FiniteDuration): JDuration = JDuration.ofNanos(finiteDuration.toNanos)
//
//  def asJavaIterable[T](iterable: Iterable[T]): JIterable[T] = asJavaIterable(iterable)
//
//  // Known as a Runnable in the java world
//  def asJavaRunnable[T](runnable: () => Unit): JRunnable = () => runnable.apply()
//
//  // Known as a Consumer in the java world
//  def asJavaConsumer[T](consumer: T => Unit): JConsumer[T] = (t: T) => consumer.apply(t)
//
//  // Known as a Function in the java world
//  def asJavaFn1[T, R](function: T => R): JFunction[T, R] = (t: T) => function.apply(t)
//
//  // Known as a BiFunction in the java world
//  def asJavaFn2[T, U, R](function: (T, U) => R): JBiFunction[T, U, R] = (t: T, u: U) => function.apply(t, u)
//
//  // Does not exist in the java world (without @FunctionalInterface), so follow convention of reactor by converting to function of Array[AnyRef]
//  def asJavaFn3[T1 >: AnyRef, T2 >: AnyRef, T3 >: AnyRef, R](function: (T1, T2, T3) => R): JFunction[Array[AnyRef], R] = (arr: Array[AnyRef]) => function.apply(arr(0), arr(1), arr(2))
//
//  // Does not exist in the java world (without @FunctionalInterface), so follow convention of reactor by converting to function of Array[AnyRef]
//  def asJavaFn4[T1 >: AnyRef, T2 >: AnyRef, T3 >: AnyRef, T4 >: AnyRef, R](function: (T1, T2, T3, T4) => R): JFunction[Array[AnyRef], R] = (arr: Array[AnyRef]) => function.apply(arr(0), arr(1), arr(2), arr(3))
//
//  // Does not exist in the java world (without @FunctionalInterface), so follow convention of reactor by converting to function of Array[AnyRef]
//  def asJavaFn5[T1 >: AnyRef, T2 >: AnyRef, T3 >: AnyRef, T4 >: AnyRef, T5 >: AnyRef, R](function: (T1, T2, T3, T4, T5) => R): JFunction[Array[AnyRef], R] = (arr: Array[AnyRef]) => function.apply(arr(0), arr(1), arr(2), arr(3), arr(4))
//
//  // Does not exist in the java world (without @FunctionalInterface), so follow convention of reactor by converting to function of Array[AnyRef]
//  def asJavaFn6[T1 >: AnyRef, T2 >: AnyRef, T3 >: AnyRef, T4 >: AnyRef, T5 >: AnyRef, T6 >: AnyRef, R](function: (T1, T2, T3, T4, T5, T6) => R): JFunction[Array[AnyRef], R] = (arr: Array[AnyRef]) => function.apply(arr(0), arr(1), arr(2), arr(3), arr(4), arr(5))

}
