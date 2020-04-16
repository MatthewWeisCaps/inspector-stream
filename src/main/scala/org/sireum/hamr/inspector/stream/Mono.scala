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

import java.lang
import java.util.concurrent.Callable
import java.util.logging.Level

import org.junit.jupiter.api.Assertions
import org.reactivestreams.{Publisher, Subscriber, Subscription}
import org.sireum.hamr.inspector.stream.JavaInterop._
import reactor.core.Disposable
import reactor.core.publisher.{MonoSink, Signal, SignalType, SynchronousSink, Flux => JFlux, Mono => JMono}
import reactor.core.scheduler.Scheduler
import reactor.util.Logger
import reactor.util.context.Context
import reactor.util.function.{Tuple2 => JTuple2}

import scala.concurrent.Future
import scala.concurrent.duration.Duration.Infinite
import scala.concurrent.duration.{Duration, FiniteDuration}
import scala.language.{existentials, higherKinds}

object Mono {

  ///
  /// API METHODS
  ///

  def create[T](callback: MonoSink[T] => Unit): Mono[T] = wrapMono(JMono.create(asJavaConsumer(callback)))
  def defer[T](supplier: () => Mono[T]): Mono[T] = wrapMono(JMono.defer(() => supplier.apply().delegate))
  def deferWithContext[T](supplier: Context => Mono[T]): Mono[T] = wrapMono(JMono.deferWithContext((c: Context) => supplier(c).delegate))

  def delay(duration: Duration): Mono[Long] = duration match {
    case _: Infinite => wrapMono(JMono.never())
    case finiteDuration: FiniteDuration => wrapMono(JMono.delay(asJavaDuration(finiteDuration)).map(Long2long))
  }

  def delay(duration: Duration, timer: Scheduler): Mono[Long] = duration match {
    case _: Infinite => wrapMono(JMono.never())
    case finiteDuration: FiniteDuration => wrapMono(JMono.delay(asJavaDuration(finiteDuration), timer).map(Long2long))
  }

  def empty[T](): Mono[T] = wrapMono(JMono.empty())

  def error[T](error: Throwable): Mono[T] = wrapMono[T](JMono.error(error))
  def error[T](errorSupplier: () => Throwable): Mono[T] = wrapMono[T](JMono.error(asJavaSupplier(errorSupplier)))

  // todo apply varargs pattern to other cases (especially needed in flux)
  def first[T](): Mono[T] = first(Seq())
  def first[T](mono: Mono[T], monos: Mono[T]*): Mono[T] = first((mono +: monos) (Seq.canBuildFrom))
  def first[T](monos: Iterable[_ <: Mono[T]]): Mono[T] = wrapMono(JMono.first(asJavaIterable(monos.map(_.delegate)(Iterable.canBuildFrom))))

  def from[T](source: Publisher[T]): Mono[T] = wrapMono(JMono.from(source))
  def fromCallable[T](supplier: Callable[T]): Mono[T] = wrapMono(JMono.fromCallable(supplier))
//  def fromCompletionStage[T](completionStage: CompletionStage[T]): Mono[T] = wrapMono(JMono.fromCompletionStage(completionStage)) // became fromFuture
//  def fromCompletionStage[T](stageSupplier: () => CompletionStage[T]): Mono[T] = wrapMono(JMono.fromCompletionStage(stageSupplier)) // became fromFuture
  def fromDirect[I](source: Publisher[I]): Mono[I] = wrapMono(JMono.fromDirect(source))
  def fromFuture[T](future: Future[T]): Mono[T] = wrapMono(JMono.fromCompletionStage(asJavaCompletionStage(future))) // completionStage is java equiv of Future
  def fromFutureSupplier[T](futureSupplier: () => Future[T]): Mono[T] = wrapMono(JMono.fromCompletionStage(() => asJavaCompletionStage(futureSupplier.apply())))
  def fromRunnable[T](runnable: () => Unit): Mono[T] = wrapMono(JMono.fromRunnable(asJavaRunnable(runnable)))
  def fromSupplier[T](supplier: () => T): Mono[T] = wrapMono(JMono.fromSupplier(asJavaSupplier(supplier)))

  def ignoreElements[T](source: Publisher[T]): Mono[T] = wrapMono(JMono.ignoreElements(source))

  def just[T](data: T): Mono[T] = wrapMono(JMono.just(data))
  def justOrEmpty[T](data: Option[T]): Mono[T] = wrapMono(JMono.justOrEmpty(asJavaOptional(data)))
  def justOrEmpty[T](data: T): Mono[T] = {
    java.util.Optional.ofNullable()
    if (data == null) {
      wrapMono(JMono.justOrEmpty(asJavaOptional(None)))
    } else {
      wrapMono(JMono.justOrEmpty(asJavaOptional(Some(data))))
    }
  }

  def never[T](): Mono[T] = wrapMono(JMono.never())

  def sequenceEqual[T](source1: Publisher[T], source2: Publisher[T]): Mono[Boolean] = wrapMono(JMono.sequenceEqual(source1, source2).asInstanceOf[JMono[Boolean]])
  def sequenceEqual[T](source1: Publisher[T], source2: Publisher[T], isEqual: (T, T) => Boolean): Mono[Boolean] = wrapMono(JMono.sequenceEqual(source1, source2, asJavaBiPredicate(isEqual)).asInstanceOf[JMono[Boolean]])
  def sequenceEqual[T](source1: Publisher[T], source2: Publisher[T], isEqual: (T, T) => Boolean, prefetch: Int): Mono[Boolean] = wrapMono(JMono.sequenceEqual(source1, source2, asJavaBiPredicate(isEqual), prefetch).asInstanceOf[JMono[Boolean]])

  def subscriberContext(): Mono[Context] = wrapMono(JMono.subscriberContext())

  // todo: look into scala Callable equivalent for using in both Mono and Flux apis
  def using[T, D](resourceSupplier: Callable[D], sourceSupplier: D => Mono[T], resourceCleanup: D => Unit): Mono[T] = wrapMono(JMono.using(resourceSupplier, asJavaFn1(sourceSupplier.andThen(_.delegate)), asJavaConsumer(resourceCleanup)))
  def using[T, D](resourceSupplier: Callable[D], sourceSupplier: D => Mono[T], resourceCleanup: D => Unit, eager: Boolean): Mono[T] = wrapMono(JMono.using(resourceSupplier, asJavaFn1(sourceSupplier.andThen(_.delegate)), asJavaConsumer(resourceCleanup), eager))

  def usingWhen[T, D](resourceSupplier: Publisher[D], resourceClosure: D => Mono[T], asyncComplete: D => Publisher[_], asyncError: D => Publisher[_]): Mono[T] = wrapMono(JMono.usingWhen(resourceSupplier, asJavaFn1(resourceClosure.andThen(_.delegate)), asJavaFn1(asyncComplete), asJavaFn1(asyncError)))
  def usingWhen[T, D](resourceSupplier: Publisher[D], resourceClosure: D => Mono[T], asyncComplete: D => Publisher[_], asyncError: D => Publisher[_], asyncCancel: D => Publisher[_]): Mono[T] = wrapMono(JMono.usingWhen(resourceSupplier, asJavaFn1(resourceClosure.andThen(_.delegate)), asJavaFn1(asyncComplete), asJavaFn1(asyncError), asJavaFn1(asyncCancel)))

  def when(): Mono[_] = when(Seq())
//  def when(source: Publisher[Any], sources: Publisher[Any]*): Mono[_] = when((source +: sources) (Seq.canBuildFrom))
  def when(sources: Publisher[_]*): Mono[_] = wrapMono(JMono.when(sources:_*))
  def when(sources: Iterable[Publisher[_]]): Mono[_] = wrapMono(JMono.when(asJavaIterable(sources)))

  def whenDelayError(): Mono[Unit] = whenDelayError(Seq())
//  def whenDelayError(source: Publisher[Any], sources: Publisher[Any]*): Mono[Unit] = whenDelayError((source +: sources) (Seq.canBuildFrom))
  def whenDelayError(sources: Publisher[_]*): Mono[Unit] = wrapMono(JMono.whenDelayError(sources:_*)).map(_ => ())
  def whenDelayError(sources: Iterable[Publisher[_]]): Mono[Unit] = wrapMono(JMono.whenDelayError(asJavaIterable(sources))).map(_ => ())

  def zip[T1, T2, O](p1: Mono[T1], p2: Mono[T2], combinator: (T1, T2) => O): Mono[O] = wrapMono(JMono.zip(p1.delegate, p2.delegate, asJavaFn2(combinator)))

  def zip[T1, T2](p1: Mono[T1], p2: Mono[T2]): Mono[(T1, T2)] = wrapMono(JMono.zip(p1.delegate, p2.delegate).map(toScalaTuple2(_)))
  def zip[T1, T2, T3](p1: Mono[T1], p2: Mono[T2], p3: Mono[T3]): Mono[(T1, T2, T3)] = wrapMono(JMono.zip(p1.delegate, p2.delegate, p3.delegate).map(toScalaTuple3(_)))
  def zip[T1, T2, T3, T4](p1: Mono[T1], p2: Mono[T2], p3: Mono[T3], p4: Mono[T4]): Mono[(T1, T2, T3, T4)] = wrapMono(JMono.zip(p1.delegate, p2.delegate, p3.delegate, p4.delegate).map(toScalaTuple4(_)))
  def zip[T1, T2, T3, T4, T5](p1: Mono[T1], p2: Mono[T2], p3: Mono[T3], p4: Mono[T4], p5: Mono[T5]): Mono[(T1, T2, T3, T4, T5)] = wrapMono(JMono.zip(p1.delegate, p2.delegate, p3.delegate, p4.delegate, p5.delegate).map(toScalaTuple5(_)))
  def zip[T1, T2, T3, T4, T5, T6](p1: Mono[T1], p2: Mono[T2], p3: Mono[T3], p4: Mono[T4], p5: Mono[T5], p6: Mono[T6]): Mono[(T1, T2, T3, T4, T5, T6)] = wrapMono(JMono.zip(p1.delegate, p2.delegate, p3.delegate, p4.delegate, p5.delegate, p6.delegate).map(toScalaTuple6(_)))
  def zip[T1, T2, T3, T4, T5, T6, T7](p1: Mono[T1], p2: Mono[T2], p3: Mono[T3], p4: Mono[T4], p5: Mono[T5], p6: Mono[T6], p7: Mono[T7]): Mono[(T1, T2, T3, T4, T5, T6, T7)] = wrapMono(JMono.zip(p1.delegate, p2.delegate, p3.delegate, p4.delegate, p5.delegate, p6.delegate, p7.delegate).map(toScalaTuple7(_)))
  def zip[T1, T2, T3, T4, T5, T6, T7, T8](p1: Mono[T1], p2: Mono[T2], p3: Mono[T3], p4: Mono[T4], p5: Mono[T5], p6: Mono[T6], p7: Mono[T7], p8: Mono[T8]): Mono[(T1, T2, T3, T4, T5, T6, T7, T8)] = wrapMono(JMono.zip(p1.delegate, p2.delegate, p3.delegate, p4.delegate, p5.delegate, p6.delegate, p7.delegate, p8.delegate).map(toScalaTuple8(_)))

  def zip[R](monos: Iterable[Mono[_]], combinator: (_ >: Array[AnyRef]) => R): Mono[R] = wrapMono(JMono.zip(asJavaIterable(monos.map(_.delegate)(Iterable.canBuildFrom)), asJavaFn1(combinator)))
  def zip[R](combinator: (_ >: Array[AnyRef]) => R, monos: Mono[_]*): Mono[R] = wrapMono(JMono.zip(asJavaFn1(combinator), monos.map(_.delegate)(Seq.canBuildFrom):_*))

  def zipDelayError[T1, T2](p1: Mono[T1], p2: Mono[T2]): Mono[(T1, T2)] = wrapMono(JMono.zipDelayError(p1.delegate, p2.delegate).map(toScalaTuple2(_)))
  def zipDelayError[T1, T2, T3](p1: Mono[T1], p2: Mono[T2], p3: Mono[T3]): Mono[(T1, T2, T3)] = wrapMono(JMono.zipDelayError(p1.delegate, p2.delegate, p3.delegate).map(toScalaTuple3(_)))
  def zipDelayError[T1, T2, T3, T4](p1: Mono[T1], p2: Mono[T2], p3: Mono[T3], p4: Mono[T4]): Mono[(T1, T2, T3, T4)] = wrapMono(JMono.zipDelayError(p1.delegate, p2.delegate, p3.delegate, p4.delegate).map(toScalaTuple4(_)))
  def zipDelayError[T1, T2, T3, T4, T5](p1: Mono[T1], p2: Mono[T2], p3: Mono[T3], p4: Mono[T4], p5: Mono[T5]): Mono[(T1, T2, T3, T4, T5)] = wrapMono(JMono.zipDelayError(p1.delegate, p2.delegate, p3.delegate, p4.delegate, p5.delegate).map(toScalaTuple5(_)))
  def zipDelayError[T1, T2, T3, T4, T5, T6](p1: Mono[T1], p2: Mono[T2], p3: Mono[T3], p4: Mono[T4], p5: Mono[T5], p6: Mono[T6]): Mono[(T1, T2, T3, T4, T5, T6)] = wrapMono(JMono.zip(p1.delegate, p2.delegate, p3.delegate, p4.delegate, p5.delegate, p6.delegate).map(toScalaTuple6(_)))
  def zipDelayError[T1, T2, T3, T4, T5, T6, T7](p1: Mono[T1], p2: Mono[T2], p3: Mono[T3], p4: Mono[T4], p5: Mono[T5], p6: Mono[T6], p7: Mono[T7]): Mono[(T1, T2, T3, T4, T5, T6, T7)] = wrapMono(JMono.zipDelayError(p1.delegate, p2.delegate, p3.delegate, p4.delegate, p5.delegate, p6.delegate, p7.delegate).map(toScalaTuple7(_)))
  def zipDelayError[T1, T2, T3, T4, T5, T6, T7, T8](p1: Mono[T1], p2: Mono[T2], p3: Mono[T3], p4: Mono[T4], p5: Mono[T5], p6: Mono[T6], p7: Mono[T7], p8: Mono[T8]): Mono[(T1, T2, T3, T4, T5, T6, T7, T8)] = wrapMono(JMono.zipDelayError(p1.delegate, p2.delegate, p3.delegate, p4.delegate, p5.delegate, p6.delegate, p7.delegate, p8.delegate).map(toScalaTuple8(_)))

  def zipDelayError[R](monos: Iterable[Mono[_]], combinator: (_ >: Array[AnyRef]) => R): Mono[R] = wrapMono(JMono.zipDelayError(asJavaIterable(monos.map(_.delegate)(Iterable.canBuildFrom)), asJavaFn1(combinator)))
  def zipDelayError[R](combinator: (_ >: Array[AnyRef]) => R, monos: Mono[_]*): Mono[R] = wrapMono(JMono.zipDelayError(asJavaFn1(combinator), monos.map(_.delegate)(Seq.canBuildFrom):_*))

}

trait Mono[T] extends Publisher[T] with ImplicitJavaInterop {

  private[stream] val delegate: JMono[T]

  ///
  /// CUSTOM METHODS
  ///

  def fail(): Mono[T] = wrapMono[T](delegate.doOnNext(it => Assertions.fail()))
  def fail(message: String): Mono[T] = wrapMono[T](delegate.doOnNext(_ => Assertions.fail(message)))
  def fail(cause: Throwable): Mono[T] = wrapMono[T](delegate.doOnNext(_ => Assertions.fail(cause)))
  def failAndSupply(messageSupplier: () => String): Mono[T] = wrapMono[T](delegate.doOnNext(_ => Assertions.fail(asJavaSupplier(messageSupplier))))
  def fail(message: String, cause: Throwable): Mono[T] = wrapMono[T](delegate.doOnNext(_ => Assertions.fail(message, cause)))

  def assertTrue(predicate: T => Boolean): Mono[T] = wrapMono[T](delegate.doOnNext(it => Assertions.assertTrue(predicate(it))))
  def assertTrue(predicate: T => Boolean, message: String): Mono[T] = wrapMono[T](delegate.doOnNext(it => Assertions.assertTrue(predicate(it), message)))
  def assertTrue(predicate: T => Boolean, messageSupplier: () => String): Mono[T] = wrapMono[T](delegate.doOnNext(it => Assertions.assertTrue(predicate(it), messageSupplier())))
  def assertTrue(predicate: T => Boolean, messageFn: T => String): Mono[T] = wrapMono[T](delegate.doOnNext(it => Assertions.assertTrue(predicate(it), messageFn(it))))

  def assertFalse(predicate: T => Boolean): Mono[T] = wrapMono[T](delegate.doOnNext(it => Assertions.assertFalse(predicate(it))))
  def assertFalse(predicate: T => Boolean, message: String): Mono[T] = wrapMono[T](delegate.doOnNext(it => Assertions.assertFalse(predicate(it), message)))
  def assertFalse(predicate: T => Boolean, messageSupplier: () => String): Mono[T] = wrapMono[T](delegate.doOnNext(it => Assertions.assertFalse(predicate(it), messageSupplier())))
  def assertFalse(predicate: T => Boolean, messageFn: T => String): Mono[T] = wrapMono[T](delegate.doOnNext(it => Assertions.assertFalse(predicate(it), messageFn(it))))

  def assertEmpty(): Mono[T] = wrapMono[T](delegate.doOnNext(it => Assertions.fail("An element was emitted despite assertEmpty() assertion")))
  def assertEmpty(message: String): Mono[T] = wrapMono[T](delegate.doOnNext(it => Assertions.fail(message)))
  def assertEmpty(cause: Throwable): Mono[T] = wrapMono[T](delegate.doOnNext(it => Assertions.fail("An element was emitted despite assertEmpty() assertion", cause)))
  def assertEmpty(message: String, cause: Throwable): Mono[T] = wrapMono[T](delegate.doOnNext(it => Assertions.fail(message, cause)))

  // in Flux, the delegate's switchIfEmpty takes a Publisher (so Flux.error works fine). In Mono, the delegate's switchIfEmpty takes a JMono
  def assertNotEmpty(): Mono[T] = wrapMono(delegate.switchIfEmpty(JMono.error(new AssertionError("Flux was empty despite assertNotEmpty() assertion"))))
  def assertNotEmpty(message: String): Mono[T] = wrapMono(delegate.switchIfEmpty(JMono.error(new AssertionError(message))))
  def assertNotEmpty(cause: Throwable): Mono[T] = wrapMono(delegate.switchIfEmpty(JMono.error(new AssertionError("Flux was empty despite assertNotEmpty() assertion", cause))))
  def assertNotEmpty(message: String, cause: Throwable): Mono[T] = wrapMono(delegate.switchIfEmpty(JMono.error(new AssertionError(message, cause))))

  ///
  /// API METHODS
  ///

  def as[P](transformer: Mono[T] => P): P = delegate.as((jm: JMono[T]) => transformer.apply(wrapMono(jm)))

  def and(other: Publisher[_]): Mono[_] = wrapMono(delegate.and(other))

  def block(): T = delegate.block()

  def block(timeout: Duration): T = timeout match {
    case _: Infinite => delegate.block()
    case finiteDuration: FiniteDuration => delegate.block(asJavaDuration(finiteDuration))
  }

  def blockOptional(): Option[T] = Option(delegate.block())

  def blockOptional(timeout: Duration): Option[T] = timeout match {
    case _: Infinite => Option(delegate.block())
    case finiteDuration: FiniteDuration => Option(delegate.block(asJavaDuration(finiteDuration)))
  }

  def cache(): Mono[T] = wrapMono[T](delegate.cache())
//  def cache(ttl: FiniteDuration): Mono[T] = wrapMono[T](delegate.cache(ttl))
//  def cache(ttl: FiniteDuration, timer: Scheduler): Mono[T] = wrapMono[T](delegate.cache(ttl, timer))
  def cache(ttlForValue: T => FiniteDuration, ttlForError: Throwable => FiniteDuration, ttlForEmpty: () => FiniteDuration): Mono[T] = wrapMono[T](delegate.cache(asJavaFn1(ttlForValue.andThen(asJavaDuration)), asJavaFn1(ttlForError.andThen(asJavaDuration)), () => asJavaDuration(ttlForEmpty())))

  // todo: allow and convert infinite durations?
  def cache(ttl: Duration): Mono[T] = ttl match {
    case _: Infinite => cache()
    case finiteDuration: FiniteDuration => wrapMono(delegate.cache(asJavaDuration(finiteDuration)))
  }
  def cache(ttl: Duration, timer: Scheduler): Mono[T] = ttl match {
    case _: Infinite => cache()
    case finiteDuration: FiniteDuration => wrapMono(delegate.cache(asJavaDuration(finiteDuration), timer))
  }

//  def cache(ttlForValue: T => Duration, ttlForError: Throwable => Duration, ttlForEmpty: () => Duration): Mono[T] = ttlForValue match {
//    case _: Infinite => ttlForError match {
//      case _: Infinite => ttlForEmpty match {
//        case _: (() => Infinite) => cache()
//        case empty: (() => FiniteDuration) => ???
//      }
//      case error: (() => FiniteDuration) => {
//        case _: (() => Infinite) => ???
//        case empty: (() => FiniteDuration) => ???
//      }
//    }
//    case value: (() => FiniteDuration) => {
//      case _: Infinite => ttlForEmpty match {
//        case _: (() => Infinite) => ???
//        case empty: (() => FiniteDuration) => ???
//      }
//      case error: (() => FiniteDuration) => {
//        case _: (() => Infinite) => ???
//        case empty: (() => FiniteDuration) => ???
//      }
//    }
//
////      wrapMono(delegate.cache(history, finiteDuration, timer))
//  }

  def cast[E](clazz: Class[E]): Mono[E] = wrapMono[E](delegate.cast(clazz))

  def checkpoint(): Mono[T] = wrapMono[T](delegate.checkpoint())
  def checkpoint(description: String): Mono[T] = wrapMono[T](delegate.checkpoint(description))
  def checkpoint(description: Option[String], forceStackTrace: Boolean): Mono[T] = description match {
    case Some(desc) => wrapMono(delegate.checkpoint(desc, forceStackTrace))
    case None => wrapMono(delegate.checkpoint(null, forceStackTrace)) // this java api accepts Nullable
  }

  def concatWith(other: Publisher[T]): Flux[T] = wrapFlux[T](delegate.concatWith(other))

  def defaultIfEmpty(defaultV: T): Mono[T] = wrapMono[T](delegate.defaultIfEmpty(defaultV))

  def delayElement(delay: FiniteDuration): Mono[T] = wrapMono[T](delegate.delayElement(asJavaDuration(delay)))
  def delayElement(delay: FiniteDuration, timer: Scheduler): Mono[T] = wrapMono[T](delegate.delayElement(asJavaDuration(delay), timer))

  def delayUntil(triggerProvider: T => Publisher[_]): Mono[T] = wrapMono[T](delegate.delayUntil(asJavaFn1(triggerProvider)))

  def delaySubscription(delay: FiniteDuration): Mono[T] = wrapMono[T](delegate.delaySubscription(asJavaDuration(delay)))
  def delaySubscription(delay: FiniteDuration, timer: Scheduler): Mono[T] = wrapMono[T](delegate.delaySubscription(asJavaDuration(delay), timer))
  def delaySubscription[U](subscriptionDelay: Publisher[U]): Mono[T] = wrapMono[T](delegate.delaySubscription(subscriptionDelay))

  def dematerialize[X](): Mono[X] = wrapMono[X](delegate.dematerialize())

  def doAfterSuccessOrError(afterSuccessOrError: (T, Throwable) => Unit): Mono[T] = wrapMono[T](delegate.doAfterSuccessOrError(asJavaBiConsumer(afterSuccessOrError)))
  def doAfterTerminate(afterTerminate: () => Unit): Mono[T] = wrapMono[T](delegate.doAfterTerminate(asJavaRunnable(afterTerminate)))
  def doFirst(onFirst: () => Unit): Mono[T] = wrapMono[T](delegate.doFirst(asJavaRunnable(onFirst)))
  def doFinally(onFinally: SignalType => Unit): Mono[T] = wrapMono[T](delegate.doFinally(asJavaConsumer(onFinally)))
  def doOnCancel(onCancel: () => Unit): Mono[T] = wrapMono[T](delegate.doOnCancel(asJavaRunnable(onCancel)))
  def doOnDiscard[R](classType: Class[R], discardHook: R => Unit): Mono[T] = wrapMono[T](delegate.doOnDiscard(classType, asJavaConsumer(discardHook)))
  def doOnNext(onNext: T => Unit): Mono[T] = wrapMono[T](delegate.doOnNext(asJavaConsumer(onNext)))
  def doOnSuccess(onSuccess: T => Unit): Mono[T] = wrapMono[T](delegate.doOnSuccess(asJavaConsumer(onSuccess)))
  def doOnEach(signalConsumer: Signal[T] => Unit): Mono[T] = wrapMono[T](delegate.doOnEach(asJavaConsumer(signalConsumer)))
  def doOnError[E <: Throwable](exceptionType: Class[E], onError: Throwable => Unit): Mono[T] = wrapMono[T](delegate.doOnError[E](exceptionType, asJavaConsumer(onError)))
  def doOnError(onError: Throwable => Unit): Mono[T] = wrapMono[T](delegate.doOnError(asJavaConsumer(onError)))
  def doOnError(predicate: Throwable => Boolean, onError: Throwable => Unit): Mono[T] = wrapMono[T](delegate.doOnError(asJavaPredicate(predicate), asJavaConsumer(onError)))
  def doOnRequest(consumer: Long => Unit): Mono[T] = wrapMono[T](delegate.doOnRequest(asJavaLongConsumer(consumer)))
  def doOnSubscribe(onSubscribe: Subscription => Unit): Mono[T] = wrapMono[T](delegate.doOnSubscribe(asJavaConsumer(onSubscribe)))
  def doOnSuccessOrError(onSuccessOrError: (T, Throwable) => Unit): Mono[T] = wrapMono[T](delegate.doOnSuccessOrError(asJavaBiConsumer(onSuccessOrError)))
  def doOnTerminate(onTerminate: () => Unit): Mono[T] = wrapMono[T](delegate.doOnTerminate(asJavaRunnable(onTerminate)))

  def elapsed(): Mono[(Long, T)] = wrapMono[JTuple2[java.lang.Long, T]](delegate.elapsed()).map(tuple => toScalaTuple2(tuple.mapT1(t1 => Long2long(t1))))
  def elapsed(scheduler: Scheduler): Mono[(Long, T)] = wrapMono[JTuple2[java.lang.Long, T]](delegate.elapsed()).map(tuple => toScalaTuple2(tuple.mapT1(t1 => Long2long(t1))))

  def expandDeep(expander: T => Publisher[T]): Flux[T] = wrapFlux[T](delegate.expandDeep(asJavaFn1(expander)))
  def expandDeep(expander: T => Publisher[T], capacityHint: Int): Flux[T] = wrapFlux[T](delegate.expandDeep(asJavaFn1(expander), capacityHint))

  def expand(expander: T => Publisher[T]): Flux[T] = wrapFlux[T](delegate.expandDeep(asJavaFn1(expander)))
  def expand(expander: T => Publisher[T], capacityHint: Int): Flux[T] = wrapFlux[T](delegate.expandDeep(asJavaFn1(expander), capacityHint))

  def filter(predicate: T => Boolean): Mono[T] = wrapMono[T](delegate.filter(asJavaPredicate(predicate)))

  def filterWhen(asyncPredicate: T => Publisher[Boolean]): Mono[T] = wrapMono[T](delegate.filterWhen((t: T) => Mono.from(asyncPredicate(t)).map(boolean2Boolean)))

  def flatMap[R](transformer: T => Mono[R]): Mono[R] = wrapMono[R](delegate.flatMap[R]((t: T) => JMono.from(transformer(t))))

  def flatMapMany[R](mapperOnNext: T => Publisher[R]): Flux[R] = wrapFlux[R](delegate.flatMapMany(asJavaFn1(mapperOnNext)))
  def flatMapMany[R](mapperOnNext: T => Publisher[R], mapperOnError: Throwable => Publisher[R], mapperOnComplete: () => Publisher[R]): Flux[R] = wrapFlux[R](delegate.flatMapMany(asJavaFn1(mapperOnNext), asJavaFn1(mapperOnError), asJavaSupplier(mapperOnComplete)))

  def flatMapIterable[R](mapper: T => Iterable[R]): Flux[R] = wrapFlux[R](delegate.flatMapIterable((t: T) => asJavaIterable[R](mapper(t))))

  def flux(): Flux[T] = wrapFlux[T](delegate.flux())

  def handle[R](handler: (T, SynchronousSink[R]) => Unit): Mono[R] = wrapMono[R](delegate.handle(asJavaBiConsumer(handler)))

  def hasElement: Mono[Boolean] = wrapMono[Boolean](delegate.hasElement.map(Boolean2boolean))

  def hide(): Mono[T] = wrapMono[T](delegate.hide())

  def ignoreElement(): Mono[T] = wrapMono[T](delegate.ignoreElement())

  def log(): Mono[T] = wrapMono[T](delegate.log())
  def log(category: String): Mono[T] = wrapMono[T](delegate.log(category))
  def log(level: Level): Mono[T] = log(null, level)
  def log(category: String, level: Level): Mono[T] = wrapMono[T](delegate.log(category, level))
  def log(category: String, level: Level, options: SignalType*): Mono[T] = wrapMono[T](delegate.log(category, level, options:_*))
  def log(level: Level, showOperatorLine: Boolean, options: SignalType*): Mono[T] = {
    val nullString: String = null // null needs a type to disambiguate method calls
    log(nullString, level, showOperatorLine, options:_*)
  }
  def log(category: String, level: Level, showOperatorLine: Boolean, options: SignalType*): Mono[T] = wrapMono[T](delegate.log(category, level, showOperatorLine, options:_*))
  def log(logger: Logger): Mono[T] = wrapMono[T](delegate.log(logger))
  def log(logger: Logger, level: Level, showOperatorLine: Boolean, options: SignalType*): Mono[T] = wrapMono[T](delegate.log(logger, level, showOperatorLine, options:_*))

  def map[R](mapper: T => R): Mono[R] = wrapMono(delegate.map(asJavaFn1(mapper)))

  def materialize(): Mono[Signal[T]] = wrapMono[Signal[T]](delegate.materialize())

  def mergeWith(other: Publisher[T]): Flux[T] = wrapFlux[T](delegate.mergeWith(other))

  def metrics(): Mono[T] = wrapMono[T](delegate.metrics())

  def name(name: String): Mono[T] = wrapMono[T](delegate.name(name))

  def or(other: Mono[T]): Mono[T] = wrapMono[T](delegate.or(other.delegate))

  def ofType[U](clazz: Class[U]): Mono[U] = wrapMono[U](delegate.ofType(clazz))

  def onErrorContinue(errorConsumer: (Throwable, Any) => Unit): Mono[T] = wrapMono[T](delegate.onErrorContinue(asJavaBiConsumer(errorConsumer)))
  def onErrorContinue[E <: Throwable](classType: Class[E], errorConsumer: (Throwable, AnyRef) => Unit): Mono[T] = wrapMono[T](delegate.onErrorContinue(classType, asJavaBiConsumer(errorConsumer)))
  def onErrorContinue[E <: Throwable](errorPredicate: E => Boolean, errorConsumer: (Throwable, AnyRef) => Unit): Mono[T] = wrapMono[T](delegate.onErrorContinue(asJavaPredicate(errorPredicate), asJavaBiConsumer(errorConsumer)))

  def onErrorStop(): Mono[T] = wrapMono[T](delegate.onErrorStop())

  def onErrorMap(mapper: Throwable => Throwable): Mono[T] = wrapMono[T](delegate.onErrorMap(asJavaFn1(mapper)))
  def onErrorMap[E <: Throwable](classType: Class[E], mapper: E => Throwable): Mono[T] = wrapMono[T](delegate.onErrorMap(classType, asJavaFn1(mapper)))
  def onErrorMap(predicate: Throwable => Boolean, mapper: Throwable => Throwable): Mono[T] = wrapMono[T](delegate.onErrorMap(asJavaFn1(mapper)))

  def onErrorResume(fallback: Throwable => Mono[T]): Mono[T] = wrapMono[T](delegate.onErrorResume((throwable: Throwable) => JMono.from(fallback(throwable))))
  def onErrorResume[E <: Throwable](classType: Class[E], fallback: E => Mono[T]): Mono[T] = wrapMono[T](delegate.onErrorResume(classType, asJavaFn1((e: E) => JMono.from(fallback(e)))))
  def onErrorResume(predicate: Throwable => Boolean, fallback: Throwable => Mono[T]): Mono[T] = wrapMono[T](delegate.onErrorResume(asJavaPredicate(predicate), asJavaFn1((throwable: Throwable) => JMono.from(fallback(throwable)))))

  def onErrorReturn(fallbackValue: T): Mono[T] = wrapMono[T](delegate.onErrorReturn(fallbackValue))
  def onErrorReturn[E <: Throwable](classType: Class[E], fallbackValue: T): Mono[T] = wrapMono[T](delegate.onErrorReturn(classType, fallbackValue))
  def onErrorReturn(predicate: Throwable => Boolean, fallbackValue: T): Mono[T] = wrapMono[T](delegate.onErrorReturn(asJavaPredicate(predicate), fallbackValue))

  def onTerminateDetach(): Mono[T] = wrapMono[T](delegate.onTerminateDetach())

  def publish[R](transform: Mono[T] => Mono[R]): Mono[R] = wrapMono[R](delegate.publish((jmono: JMono[T]) => JMono.from[R](transform(Mono.from[T](jmono)))))

  def publishOn(scheduler: Scheduler): Mono[T] = wrapMono[T](delegate.publishOn(scheduler))

  def repeat(): Flux[T] = wrapFlux[T](delegate.repeat())
  def repeat(predicate: () => Boolean): Flux[T] = wrapFlux[T](delegate.repeat(asJavaBooleanSupplier(predicate)))
  def repeat(numRepeat: Long): Flux[T] = wrapFlux[T](delegate.repeat(numRepeat))
  def repeat(numRepeat: Long, predicate: () => Boolean): Flux[T] = wrapFlux[T](delegate.repeat(numRepeat, asJavaBooleanSupplier(predicate)))

//  def repeatWhen(repeatFactory: Flux[Long] => Publisher[_]): Flux[T] = wrapFlux[T](delegate.repeatWhen((flux: JFlux[lang.Long]) => repeatFactory(wrapFlux(flux).map(Long2long))))
  def repeatWhen(repeatFactory: Flux[Long] => Publisher[_]): Flux[T] = {
    val fn = (f: JFlux[lang.Long]) => repeatFactory.apply(wrapFlux(f.map(long => long2Long(long))))
    wrapFlux[T](delegate.repeatWhen(asJavaFn1(fn)))
  }

//  def repeatWhenEmpty(repeatFactory: Flux[Long] => Publisher[_]): Mono[T] = wrapMono[T](delegate.repeatWhenEmpty((flux: JFlux[lang.Long]) => wrapFlux(flux).map(Long2long)))
  def repeatWhenEmpty(repeatFactory: Flux[Long] => Publisher[_]): Mono[T] = {
    val fn = (f: JFlux[lang.Long]) => repeatFactory.apply(wrapFlux(f.map(long => long2Long(long))))
    wrapMono[T](delegate.repeatWhenEmpty(asJavaFn1(fn)))
  }
//  def repeatWhenEmpty(maxRepeat: Int, repeatFactory: Flux[Long] => Publisher[_]): Mono[T] = wrapMono[T](delegate.repeatWhenEmpty(maxRepeat, (flux: JFlux[lang.Long]) => wrapFlux(flux).map(Long2long)))
  def repeatWhenEmpty(maxRepeat: Int, repeatFactory: Flux[Long] => Publisher[_]): Mono[T] = {
    val fn = (f: JFlux[lang.Long]) => repeatFactory.apply(wrapFlux(f.map(long => long2Long(long))))
    wrapMono[T](delegate.repeatWhenEmpty(maxRepeat, asJavaFn1(fn)))
  }

  def retry(): Mono[T] = wrapMono[T](delegate.retry())
  def retry(numRetries: Long): Mono[T] = wrapMono[T](delegate.retry(numRetries))
  def retry(retryMatcher: Throwable => Boolean): Mono[T] = wrapMono[T](delegate.retry(asJavaPredicate(retryMatcher)))
  def retry(numRetries: Long, retryMatcher: Throwable => Boolean): Mono[T] = wrapMono[T](delegate.retry(numRetries, asJavaPredicate(retryMatcher)))
  def retryWhen(whenFactory: Flux[Throwable] => Publisher[_]): Mono[T] = wrapMono[T](delegate.retryWhen((flux: JFlux[Throwable]) => whenFactory(wrapFlux(flux))))

  def retryBackoff(numRetries: Long, firstBackoff: FiniteDuration): Mono[T] = wrapMono[T](delegate.retryBackoff(numRetries, asJavaDuration(firstBackoff)))
  def retryBackoff(numRetries: Long, firstBackoff: FiniteDuration, maxBackoff: FiniteDuration): Mono[T] = wrapMono[T](delegate.retryBackoff(numRetries, asJavaDuration(firstBackoff), asJavaDuration(maxBackoff)))
  def retryBackoff(numRetries: Long, firstBackoff: FiniteDuration, maxBackoff: FiniteDuration, backoffScheduler: Scheduler): Mono[T] = wrapMono[T](delegate.retryBackoff(numRetries, asJavaDuration(firstBackoff), asJavaDuration(maxBackoff), backoffScheduler))
  def retryBackoff(numRetries: Long, firstBackoff: FiniteDuration, maxBackoff: FiniteDuration, jitterFactor: Double): Mono[T] = wrapMono[T](delegate.retryBackoff(numRetries, asJavaDuration(firstBackoff), asJavaDuration(maxBackoff), jitterFactor))
  def retryBackoff(numRetries: Long, firstBackoff: FiniteDuration, maxBackoff: FiniteDuration, jitterFactor: Double, backoffScheduler: Scheduler): Mono[T] = wrapMono[T](delegate.retryBackoff(numRetries, asJavaDuration(firstBackoff), asJavaDuration(maxBackoff), jitterFactor, backoffScheduler))

  def single(): Mono[T] = wrapMono[T](delegate.single())

  def subscribe(): Disposable = delegate.subscribe()
  def subscribe(consumer: T => Unit): Disposable = delegate.subscribe(asJavaConsumer(consumer))
  def subscribe(consumer: T => Unit, errorConsumer: Throwable => Unit): Disposable = delegate.subscribe(asJavaConsumer(consumer), asJavaConsumer(errorConsumer))
  def subscribe(consumer: T => Unit, errorConsumer: Throwable => Unit, completeConsumer: () => Unit): Disposable = delegate.subscribe(asJavaConsumer(consumer), asJavaConsumer(errorConsumer), asJavaRunnable(completeConsumer))
  def subscribe(consumer: T => Unit, errorConsumer: Throwable => Unit, completeConsumer: () => Unit, subscriptionConsumer: Subscription => Unit): Disposable = delegate.subscribe(asJavaConsumer(consumer), asJavaConsumer(errorConsumer), asJavaRunnable(completeConsumer), asJavaConsumer(subscriptionConsumer))
  def subscribe(consumer: T => Unit, errorConsumer: Throwable => Unit, completeConsumer: () => Unit, initialContext: Context): Disposable = delegate.subscribe(asJavaConsumer(consumer), asJavaConsumer(errorConsumer), asJavaRunnable(completeConsumer), initialContext)

  ///
  /// BRIDGE METHOD Subscribe
  /// (if unsure, it's almost never correct to call this method directly!)
  ///
  override def subscribe(s: Subscriber[_ >: T]): Unit = delegate.subscribe(s)
  ///
  ///
  ///

  def subscriberContext(mergeContext: Context): Mono[T] = wrapMono[T](delegate.subscriberContext(mergeContext))
  def subscriberContext(doOnContext: Context => Context): Mono[T] = wrapMono[T](delegate.subscriberContext(asJavaFn1(doOnContext)))

  def subscribeOn(scheduler: Scheduler): Mono[T] = wrapMono[T](delegate.subscribeOn(scheduler))

  def subscribeWith[E <: Subscriber[T]](subscriber: E): E = delegate.subscribeWith(subscriber)

  def switchIfEmpty(alternate: Mono[T]): Mono[T] = wrapMono[T](delegate.switchIfEmpty(alternate.delegate))

  def tag(key: String, value: String): Mono[T] = wrapMono[T](delegate.tag(key, value))

  def take(timespan: FiniteDuration): Mono[T] = wrapMono[T](delegate.take(asJavaDuration(timespan)))
  def take(timespan: FiniteDuration, scheduler: Scheduler): Mono[T] = wrapMono[T](delegate.take(asJavaDuration(timespan), scheduler))

  def takeUntilOther(other: Publisher[_]): Mono[T] = wrapMono[T](delegate.takeUntilOther(other))

  def then(): Mono[Unit] = wrapMono[Void](delegate.`then`()).map(_ => ())
  def then[V](other: Mono[V]): Mono[V] = wrapMono[V](delegate.`then`(other.delegate.asInstanceOf[JMono[V]]))

  def thenReturn[V](value: V): Mono[V] = wrapMono[V](delegate.thenReturn(value))
  def thenEmpty(other: Publisher[Unit]): Mono[Unit] = wrapMono[Unit](delegate.thenEmpty(Mono.from(other).map[Void](_ => null: Void)).map(_ => Unit))
  def thenMany[V](other: Publisher[V]): Flux[V] = wrapFlux[V](delegate.thenMany(other))

  def timeout(timeout: FiniteDuration): Mono[T] = wrapMono[T](delegate.timeout(asJavaDuration(timeout)))
  def timeout(timeout: FiniteDuration, fallback: Mono[T]): Mono[T] = wrapMono[T](delegate.timeout(asJavaDuration(timeout), fallback.delegate))
  def timeout(timeout: FiniteDuration, timer: Scheduler): Mono[T] = wrapMono[T](delegate.timeout(asJavaDuration(timeout), timer))
  def timeout(timeout: FiniteDuration, fallback: Mono[T], timer: Scheduler): Mono[T] = wrapMono[T](delegate.timeout(asJavaDuration(timeout), fallback.delegate, timer))
  def timeout[U](firstTimeout: Publisher[U]): Mono[T] = wrapMono[T](delegate.timeout[U](firstTimeout))
  def timeout[U](firstTimeout: Publisher[U], fallback: Mono[T]): Mono[T] = wrapMono[T](delegate.timeout[U](firstTimeout, fallback.delegate))

  def timestamp(): Mono[(Long, T)] = wrapMono[(Long, T)](delegate.timestamp().map(t => toScalaTuple2(t.mapT1(t1 => Long2long(t1)))))
  def timestamp(scheduler: Scheduler): Mono[(Long, T)] = wrapMono[(Long, T)](delegate.timestamp(scheduler).map(t => toScalaTuple2(t.mapT1(t1 => Long2long(t1)))))

  def toFuture: Future[T] = toScalaFuture(delegate.toFuture)

  // todo add monoProcessor wrapper
//  def toProcessor: MonoProcessor[T] = wrapMonoProcessor(delegate.toProcessor)

  def transform[V](transformer: Mono[T] => Publisher[V]): Mono[V] = wrapMono(delegate.transform[V](asJavaFn1((jMono: JMono[T]) => transformer(wrapMono(jMono)))))
  def transformDeferred[V](transformer: Mono[T] => Publisher[V]): Mono[V] = wrapMono(delegate.transformDeferred[V](asJavaFn1((jMono: JMono[T]) => transformer(wrapMono(jMono)))))

  def zipWhen[T2](rightGenerator: T => Mono[T2]): Mono[(T, T2)] = wrapMono[(T, T2)](delegate.zipWhen[T2]((t: T) => rightGenerator(t).delegate).map(toScalaTuple2(_)))
  def zipWhen[T2, O](rightGenerator: T => Mono[T2], combinator: (T, T2) => O): Mono[O] = wrapMono[O](delegate.zipWhen[T2, O]((t: T) => rightGenerator(t).delegate, asJavaFn2(combinator)))

  def zipWith[T2](other: Mono[T2]): Mono[(T, T2)] = wrapMono[(T, T2)](delegate.zipWith(other.delegate).map(toScalaTuple2(_)))
  def zipWith[T2, O](other: Mono[T2], combinator: (T, T2) => O): Mono[O] = wrapMono[O](delegate.zipWith(other.delegate, asJavaFn2(combinator)))

}

private[stream] class MonoImpl[T](publisher: Publisher[T]) extends Mono[T] {
  override private[stream] val delegate = JMono.from(publisher)
}