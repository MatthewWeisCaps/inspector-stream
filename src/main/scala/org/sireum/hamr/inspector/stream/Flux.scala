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

import java.util.concurrent.Callable
import java.util.function.BiFunction
import java.util.logging.Level
import java.util.stream.Collector
import java.{lang, util}

import org.junit.jupiter.api.Assertions
import org.reactivestreams.{Publisher, Subscriber, Subscription}
import org.sireum.hamr.inspector.stream.JavaInterop._
import reactor.core.publisher.{BufferOverflowStrategy, Signal, SignalType, SynchronousSink, Flux => JFlux, FluxSink => JFluxSink, Mono => JMono}
import reactor.core.scheduler.Scheduler
import reactor.core.{Disposable, Scannable => JScannable}
import reactor.util.Logger
import reactor.util.context.Context
import reactor.util.function.{Tuple2 => JTuple2, Tuple3 => JTuple3, Tuple4 => JTuple4, Tuple5 => JTuple5, Tuple6 => JTuple6, Tuple7 => JTuple7, Tuple8 => JTuple8}

import scala.collection.mutable
import scala.concurrent.duration.Duration.Infinite
import scala.concurrent.duration.{Duration, FiniteDuration}
import scala.language.{existentials, higherKinds}

object Flux {

  ///
  /// API METHODS
  ///

  def combineLatest[T, V](combinator: Array[AnyRef] => V, sources: Publisher[T]*): Flux[V] = wrapFlux[V](JFlux.combineLatest(asJavaFn1(combinator), sources:_*))
  def combineLatest[T, V](combinator: Array[AnyRef] => V, prefetch: Int, sources: Publisher[T]*): Flux[V] = wrapFlux[V](JFlux.combineLatest(asJavaFn1(combinator), prefetch, sources:_*))
  def combineLatest[T1, T2, V](source1: Publisher[T1], source2: Publisher[T2], combinator: (T1, T2) => _ <: V): Flux[V] = wrapFlux[V](JFlux.combineLatest(source1, source2, asJavaFn2(combinator)))
  def combineLatest[T1, T2, T3, V](source1: Publisher[T1], source2: Publisher[T2], source3: Publisher[T3], combinator: (T1, T2, T3) => V): Flux[V] = wrapFlux[V](JFlux.combineLatest(source1, source2, source3, asJavaFn3(combinator)))
  def combineLatest[T1, T2, T3, T4, V](source1: Publisher[T1], source2: Publisher[T2], source3: Publisher[T3], source4: Publisher[T4], combinator: (T1, T2, T3, T4) => V): Flux[V] = wrapFlux[V](JFlux.combineLatest(source1, source2, source3, source4, asJavaFn4(combinator)))
  def combineLatest[T1, T2, T3, T4, T5, V](source1: Publisher[T1], source2: Publisher[T2], source3: Publisher[T3], source4: Publisher[T4], source5: Publisher[T5], combinator: (T1, T2, T3, T4, T5) => V): Flux[V] = wrapFlux[V](JFlux.combineLatest(source1, source2, source3, source4, source5, asJavaFn5(combinator)))
  def combineLatest[T1, T2, T3, T4, T5, T6, V](source1: Publisher[T1], source2: Publisher[T2], source3: Publisher[T3], source4: Publisher[T4], source5: Publisher[T5], source6: Publisher[T6], combinator: (T1, T2, T3, T4, T5, T6) => V): Flux[V] = wrapFlux[V](JFlux.combineLatest(source1, source2, source3, source4, source5, source6, asJavaFn6(combinator)))

  def combineLatest[T, V](sources: Iterable[_ <: Publisher[T]], combinator: Array[AnyRef] => V): Flux[V] = wrapFlux[V](JFlux.combineLatest(asJavaIterable(sources), asJavaFn1(combinator)))
  def combineLatest[T, V](sources: Iterable[_ <: Publisher[T]], prefetch: Int, combinator: Array[AnyRef] => V): Flux[V] = wrapFlux[V](JFlux.combineLatest(asJavaIterable(sources), prefetch, asJavaFn1(combinator)))

  def concat[T](sources: Iterable[_ <: Publisher[T]]): Flux[T] = wrapFlux[T](JFlux.concat(asJavaIterable(sources)))
  def concatWithValues[T](sources: Publisher[T]*): Flux[T] = wrapFlux[T](JFlux.concat(sources:_*))
  def concat[T](sources: Publisher[_ <: Publisher[T]]): Flux[T] = wrapFlux[T](JFlux.concat(sources))
  def concat[T](sources: Publisher[_ <: Publisher[T]], prefetch: Int): Flux[T] = wrapFlux[T](JFlux.concat(sources, prefetch))
  def concat[T](sources: Publisher[T]*): Flux[T] = wrapFlux[T](JFlux.concat(sources:_*))

  def concatDelayError[T](sources: Publisher[_ <: Publisher[T]]): Flux[T] = wrapFlux[T](JFlux.concatDelayError(sources))
  def concatDelayError[T](sources: Publisher[_ <: Publisher[T]], prefetch: Int): Flux[T] = wrapFlux[T](JFlux.concatDelayError(sources, prefetch))
  def concatDelayError[T](sources: Publisher[_ <: Publisher[T]], delayUntilEnd: Boolean, prefetch: Int): Flux[T] = wrapFlux[T](JFlux.concatDelayError(sources, delayUntilEnd, prefetch))
  def concatDelayError[T](sources: Publisher[T]*): Flux[T] = wrapFlux[T](JFlux.concatDelayError(sources:_*))

  def create[T](emitter: FluxSink[T] => Unit): Flux[T] = wrapFlux[T](JFlux.create((jfluxSink: JFluxSink[T]) => emitter(FluxSink.wrap(jfluxSink))))
  def create[T](emitter: FluxSink[T] => Unit, backpressure: FluxSink.OverflowStrategy): Flux[T] = wrapFlux[T](JFlux.create((jfluxSink: JFluxSink[T]) => emitter(FluxSink.wrap(jfluxSink)), backpressure.asInstanceOf[JFluxSink.OverflowStrategy]))

  def push[T](emitter: FluxSink[T] => Unit): Flux[T] = wrapFlux[T](JFlux.push((jfluxSink: JFluxSink[T]) => emitter(FluxSink.wrap(jfluxSink))))
  def push[T](emitter: FluxSink[T] => Unit, backpressure: FluxSink.OverflowStrategy): Flux[T] = wrapFlux[T](JFlux.push((jfluxSink: JFluxSink[T]) => emitter(FluxSink.wrap(jfluxSink)), backpressure.asInstanceOf[JFluxSink.OverflowStrategy]))

  def defer[T](supplier: () => Publisher[T]): Flux[T] = wrapFlux[T](JFlux.defer(asJavaSupplier(supplier)))
  def deferWithContext[T](supplier: Context => Publisher[T]): Flux[T] = wrapFlux[T](JFlux.deferWithContext(asJavaFn1(supplier)))

  def empty[T](): Flux[T] = wrapFlux[T](JFlux.empty())

  def error[T](error: Throwable): Flux[T] = wrapFlux[T](JFlux.error(error))
  def error[T](errorSupplier: () => Throwable): Flux[T] = wrapFlux[T](JFlux.error(asJavaSupplier(errorSupplier)))
  def error[T](error: Throwable, whenRequested: Boolean): Flux[T] = wrapFlux[T](JFlux.error(error, whenRequested))

  def first[T](source: Publisher[T], sources: Publisher[T]*): Flux[T] = first((source +: sources) (Seq.canBuildFrom))
  def first[T](sources: Iterable[_ <: Publisher[T]]): Flux[T] = wrapFlux[T](JFlux.first(asJavaIterable(sources)))

  def from[T](source: Publisher[T]): Flux[T] = wrapFlux[T](JFlux.from(source))
  def fromArray[T](source: Array[T with Object]): Flux[T] = wrapFlux[T](JFlux.fromArray(source))
  def fromIterable[T](source: Iterable[T]): Flux[T] = wrapFlux[T](JFlux.fromIterable(asJavaIterable(source)))

  // scala stream can't directly map to java stream, so ignore this (since fromIterable will work with this)
//  def fromStream[T](stream: Stream[T]): Flux[T] = wrap(JFlux.fromStream(stream))
//  def fromStream[T](stream: Stream[T]): Flux[T] = wrapFlux[T](JFlux.fromIterable(asJavaIterable(stream)))
  def fromStream[T](stream: Stream[T]): Flux[T] = wrapFlux[T](JFlux.fromStream(toJavaStream(stream)))

//  // todo
//  def fromStream[T](streamSupplier: () => Stream[T]): Flux[T] = {
//    //    val ss: java.util.function.Supplier[java.util.stream.Stream[_ <: T]] = asJavaSupplier(() => toJavaStream(streamSupplier.apply()))
//    //    val st: JFlux[T] = JFlux.fromStream(ss)
//        val value: Supplier[stream.Stream[T]] = asJavaSupplier(() => toJavaStream(streamSupplier.apply()))
//    wrapFlux(JFlux.fromStream(value))
//  }

  def generate[T](generator: SynchronousSink[T] => Unit): Flux[T] = wrapFlux[T](JFlux.generate(asJavaConsumer(generator)))
  def generate[T, S](stateSupplier: () => S, generator: (S, SynchronousSink[T]) => S): Flux[T] = wrapFlux[T](JFlux.generate(asJavaCallable(stateSupplier), asJavaFn2(generator)))
  def generate[T, S](stateSupplier: () => S, generator: (S, SynchronousSink[T]) => S, stateConsumer: S => Unit): Flux[T] = wrapFlux[T](JFlux.generate(asJavaCallable(stateSupplier), asJavaFn2(generator), asJavaConsumer(stateConsumer)))


  def interval[T, S](period: Duration): Flux[Long] = period match {
    case _: Infinite => wrapFlux(JFlux.never())
    case finiteDuration: FiniteDuration => wrapFlux(JFlux.interval(asJavaDuration(finiteDuration)).map(Long2long))
  }

  def interval[T, S](delay: Duration, period: Duration): Flux[Long] = (delay, period) match {
    case (_: Infinite, _: Infinite) => wrapFlux(JFlux.never())
    case (_: Infinite, period: FiniteDuration) => wrapFlux(JFlux.never())
    case (delay: FiniteDuration, _: Infinite) => Mono.just(0L).delayElement(delay).concatWith(Flux.never())
    case (delay: FiniteDuration, period: FiniteDuration) => wrapFlux(JFlux.interval(asJavaDuration(delay), asJavaDuration(period)).map(Long2long))
  }

  def interval[T, S](period: Duration, timer: Scheduler): Flux[Long] = period match {
    case _: Infinite => wrapFlux(JFlux.never())
    case finiteDuration: FiniteDuration => wrapFlux(JFlux.interval(asJavaDuration(finiteDuration), timer).map(Long2long))
  }

  def interval[T, S](delay: Duration, period: Duration, timer: Scheduler): Flux[Long] = (delay, period) match {
    case (_: Infinite, _: Infinite) => wrapFlux(JFlux.never())
    case (_: Infinite, period: FiniteDuration) => wrapFlux(JFlux.never())
    case (delay: FiniteDuration, _: Infinite) => Mono.just(0L).delayElement(delay).concatWith(Flux.never())
    case (delay: FiniteDuration, period: FiniteDuration) => wrapFlux(JFlux.interval(asJavaDuration(delay), asJavaDuration(period), timer).map(Long2long))
  }

  def just[T](data: T*): Flux[T] = wrapFlux[T](JFlux.just(data:_*))

  def merge[T](source: Publisher[Publisher[T]]): Flux[T] = wrapFlux[T](JFlux.merge(source))
  def merge[T](source: Publisher[Publisher[T]], concurrency: Int): Flux[T] = wrapFlux[T](JFlux.merge(source, concurrency))
  def merge[T](source: Publisher[Publisher[T]], concurrency: Int, prefetch: Int): Flux[T] = wrapFlux[T](JFlux.merge(source, concurrency, prefetch))
  def merge[T](sources: Iterable[Publisher[T]]): Flux[T] = wrapFlux[T](JFlux.merge(asJavaIterable(sources)))
  def merge[T](sources: Publisher[T]*): Flux[T] = wrapFlux[T](JFlux.merge(sources:_*))
  def merge[T](prefetch: Int, sources: Publisher[T]*): Flux[T] = wrapFlux[T](JFlux.merge(sources:_*))

  def mergeDelayError[T](prefetch: Int, sources: Publisher[T]*): Flux[T] = wrapFlux[T](JFlux.mergeDelayError(prefetch, sources:_*))

  // todo reason to have T in publisher? compiles either way and reduces freedom?
//  def mergeOrdered[T <: Ordered[T]](sources: Publisher[Ordered[T]]): Flux[T] = wrapFlux[T](JFlux.mergeOrdered(sources))
//  def mergeOrdered[T](ordering: Ordering[T], sources: Publisher[T]*): Flux[T] = wrapFlux[T](JFlux.mergeOrdered(ordering, sources:_*))
  def mergeOrdered[T](ordering: Ordering[T], sources: Publisher[T]*): Flux[T] = wrapFlux[T](JFlux.mergeOrdered(ordering, sources:_*))
//  def mergeOrderedPrefetch[T](prefetch: Int, ordering: Ordering[T], sources: Publisher[T]*): Flux[T] = wrapFlux[T](JFlux.mergeOrdered(prefetch, ordering, sources:_*))
  def mergeOrderedPrefetch[T](prefetch: Int, ordering: Ordering[T], sources: Publisher[T]*): Flux[T] = wrapFlux[T](JFlux.mergeOrdered(prefetch, ordering, sources:_*))

  def mergeSequential[T](sources: Publisher[_ <: Publisher[T]]): Flux[T] = wrapFlux[T](JFlux.mergeSequential(sources))
  def mergeSequential[T](sources: Publisher[_ <: Publisher[T]], maxConcurrency: Int, prefetch: Int): Flux[T] = wrapFlux[T](JFlux.mergeSequential(sources, maxConcurrency, prefetch))
  def mergeSequentialDelayError[T](sources: Publisher[_ <: Publisher[T]], maxConcurrency: Int, prefetch: Int): Flux[T] = wrapFlux[T](JFlux.mergeSequentialDelayError(sources, maxConcurrency, prefetch))
  // todo
  def mergeSequentialVarargs[T](sources: Publisher[T]*): Flux[T] = wrapFlux[T](JFlux.mergeSequential(sources:_*))
  def mergeSequential[T](prefetch: Int, sources: Publisher[T]*): Flux[T] = wrapFlux[T](JFlux.mergeSequential(prefetch, sources:_*))
  def mergeSequentialDelayError[T](prefetch: Int, sources: Publisher[T]*): Flux[T] = wrapFlux[T](JFlux.mergeSequentialDelayError(prefetch, sources:_*))
  def mergeSequential[T](sources: Iterable[_ <: Publisher[T]]): Flux[T] = wrapFlux[T](JFlux.mergeSequential(asJavaIterable(sources)))
  def mergeSequential[T](sources: Iterable[_ <: Publisher[T]], maxConcurrency: Int, prefetch: Int): Flux[T] = wrapFlux[T](JFlux.mergeSequential(asJavaIterable(sources), maxConcurrency, prefetch))
  def mergeSequentialDelayError[T](sources: Iterable[_ <: Publisher[T]], maxConcurrency: Int, prefetch: Int): Flux[T] = wrapFlux[T](JFlux.mergeSequentialDelayError(asJavaIterable(sources), maxConcurrency, prefetch))

  def never[T](): Flux[T] = wrapFlux[T](JFlux.never())

  def range(start: Int, count: Int): Flux[Int] = wrapFlux[Int](JFlux.range(start, count).map(Integer2int(_)))

  def switchOnNext[T](mergedPublishers: Publisher[_ <: Publisher[T]]): Flux[T] = wrapFlux[T](JFlux.switchOnNext(mergedPublishers))
  def switchOnNext[T](mergedPublishers: Publisher[_ <: Publisher[T]], prefetch: Int): Flux[T] = wrapFlux[T](JFlux.switchOnNext(mergedPublishers, prefetch))

  def using[T, D](resourceSupplier: Callable[D], sourceSupplier: D => Publisher[T], resourceCleanup: D => Unit): Flux[T] = wrapFlux[T](JFlux.using(resourceSupplier, asJavaFn1(sourceSupplier), asJavaConsumer(resourceCleanup)))
  def using[T, D](resourceSupplier: Callable[D], sourceSupplier: D => Publisher[T], resourceCleanup: D => Unit, eager: Boolean): Flux[T] = wrapFlux[T](JFlux.using(resourceSupplier, asJavaFn1(sourceSupplier), asJavaConsumer(resourceCleanup), eager))

  def usingWhen[T, D](resourceSupplier: Publisher[D], resourceClosure: D => Publisher[T], asyncComplete: D => Publisher[_], asyncError: D => Publisher[_]): Flux[T] = wrapFlux[T](JFlux.usingWhen(resourceSupplier, asJavaFn1(resourceClosure), asJavaFn1(asyncComplete), asJavaFn1(asyncError)))
  def usingWhen[T, D](resourceSupplier: Publisher[D], resourceClosure: D => Publisher[T], asyncComplete: D => Publisher[_], asyncError: D => Publisher[_], asyncCancel: D => Publisher[_]): Flux[T] = wrapFlux[T](JFlux.usingWhen(resourceSupplier, asJavaFn1(resourceClosure), asJavaFn1(asyncComplete), asJavaFn1(asyncError), asJavaFn1(asyncCancel)))
  def usingWhen[T, D](resourceSupplier: Publisher[D], resourceClosure: D => Publisher[T], asyncCleanup: D => Publisher[_]): Flux[T] = wrapFlux[T](JFlux.usingWhen(resourceSupplier, asJavaFn1(resourceClosure), asJavaFn1(asyncCleanup)))

  def zip[T1, T2, O](source1: Publisher[T1], source2: Publisher[T2], combinator: (T1, T2) => O): Flux[O] = wrapFlux[O](JFlux.zip(source1, source2, asJavaFn2(combinator)))

  def zip[T1, T2](source1: Publisher[T1], source2: Publisher[T2]): Flux[(T1, T2)] = wrapFlux(JFlux.zip(source1, source2).map(toScalaTuple2(_)))
  def zip[T1, T2, T3](source1: Publisher[T1], source2: Publisher[T2], source3: Publisher[T3]): Flux[(T1, T2, T3)] = wrapFlux(JFlux.zip(source1, source2, source3).map(toScalaTuple3(_)))
  def zip[T1, T2, T3, T4](source1: Publisher[T1], source2: Publisher[T2], source3: Publisher[T3], source4: Publisher[T4]): Flux[(T1, T2, T3, T4)] = wrapFlux(JFlux.zip(source1, source2, source3, source4).map(toScalaTuple4(_)))
  def zip[T1, T2, T3, T4, T5](source1: Publisher[T1], source2: Publisher[T2], source3: Publisher[T3], source4: Publisher[T4], source5: Publisher[T5]): Flux[(T1, T2, T3, T4, T5)] = wrapFlux(JFlux.zip(source1, source2, source3, source4, source5).map(toScalaTuple5(_)))
  def zip[T1, T2, T3, T4, T5, T6](source1: Publisher[T1], source2: Publisher[T2], source3: Publisher[T3], source4: Publisher[T4], source5: Publisher[T5], source6: Publisher[T6]): Flux[(T1, T2, T3, T4, T5, T6)] = wrapFlux(JFlux.zip(source1, source2, source3, source4, source5, source6).map(toScalaTuple6(_)))
  def zip[T1, T2, T3, T4, T5, T6, T7](source1: Publisher[T1], source2: Publisher[T2], source3: Publisher[T3], source4: Publisher[T4], source5: Publisher[T5], source6: Publisher[T6], source7: Publisher[T7]): Flux[(T1, T2, T3, T4, T5, T6, T7)] = wrapFlux(JFlux.zip(source1, source2, source3, source4, source5, source6, source7).map(toScalaTuple7(_)))
  def zip[T1, T2, T3, T4, T5, T6, T7, T8](source1: Publisher[T1], source2: Publisher[T2], source3: Publisher[T3], source4: Publisher[T4], source5: Publisher[T5], source6: Publisher[T6], source7: Publisher[T7], source8: Publisher[T8]): Flux[(T1, T2, T3, T4, T5, T6, T7, T8)] = wrapFlux(JFlux.zip(source1, source2, source3, source4, source5, source6, source7, source8).map(toScalaTuple8(_)))

  def zip[O](sources: Iterable[Publisher[_]], combinator: (_ >: Array[AnyRef]) => O): Flux[O] = wrapFlux[O](JFlux.zip(asJavaIterable(sources), asJavaFn1(combinator)))
  def zip[O](sources: Iterable[Publisher[_]], prefetch: Int, combinator: (_ >: Array[AnyRef]) => O): Flux[O] = wrapFlux[O](JFlux.zip(asJavaIterable(sources), prefetch, asJavaFn1(combinator)))

  def zip[I, O](combinator: (_ >: Array[AnyRef]) => O, sources: Publisher[I]*): Flux[O] = wrapFlux[O](JFlux.zip(asJavaFn1(combinator), sources:_*))
  def zip[I, O](combinator: (_ >: Array[AnyRef]) => O, prefetch: Int, sources: Publisher[I]*): Flux[O] = wrapFlux[O](JFlux.zip(asJavaFn1(combinator), prefetch, sources:_*))

  def zip[V](sources: Publisher[_ <: Publisher[_]], combinator: (Any, Any) => V): Flux[V] = wrapFlux[V](JFlux.zip(sources, asJavaFn1((tuple: JTuple2[Any, Any]) => combinator(tuple.getT1, tuple.getT2))))
  def zip[V](sources: Publisher[_ <: Publisher[_]], combinator: (Any, Any, Any) => V): Flux[V] = wrapFlux[V](JFlux.zip(sources, asJavaFn1((tuple: JTuple3[Any, Any, Any]) => combinator(tuple.getT1, tuple.getT2, tuple.getT3))))
  def zip[V](sources: Publisher[_ <: Publisher[_]], combinator: (Any, Any, Any, Any) => V): Flux[V] = wrapFlux[V](JFlux.zip(sources, asJavaFn1((tuple: JTuple4[Any, Any, Any, Any]) => combinator(tuple.getT1, tuple.getT2, tuple.getT3, tuple.getT4))))
  def zip[V](sources: Publisher[_ <: Publisher[_]], combinator: (Any, Any, Any, Any, Any) => V): Flux[V] = wrapFlux[V](JFlux.zip(sources, asJavaFn1((tuple: JTuple5[Any, Any, Any, Any, Any]) => combinator(tuple.getT1, tuple.getT2, tuple.getT3, tuple.getT4, tuple.getT5))))
  def zip[V](sources: Publisher[_ <: Publisher[_]], combinator: (Any, Any, Any, Any, Any, Any) => V): Flux[V] = wrapFlux[V](JFlux.zip(sources, asJavaFn1((tuple: JTuple6[Any, Any, Any, Any, Any, Any]) => combinator(tuple.getT1, tuple.getT2, tuple.getT3, tuple.getT4, tuple.getT5, tuple.getT6))))
  def zip[V](sources: Publisher[_ <: Publisher[_]], combinator: (Any, Any, Any, Any, Any, Any, Any) => V): Flux[V] = wrapFlux[V](JFlux.zip(sources, asJavaFn1((tuple: JTuple7[Any, Any, Any, Any, Any, Any, Any]) => combinator(tuple.getT1, tuple.getT2, tuple.getT3, tuple.getT4, tuple.getT5, tuple.getT6, tuple.getT7))))
  def zip[V](sources: Publisher[_ <: Publisher[_]], combinator: (Any, Any, Any, Any, Any, Any, Any, Any) => V): Flux[V] = wrapFlux[V](JFlux.zip(sources, asJavaFn1((tuple: JTuple8[Any, Any, Any, Any, Any, Any, Any, Any]) => combinator(tuple.getT1, tuple.getT2, tuple.getT3, tuple.getT4, tuple.getT5, tuple.getT6, tuple.getT7, tuple.getT8))))

}

trait Flux[T] extends Publisher[T] with ImplicitJavaInterop {

  private[stream] val delegate: JFlux[T]

  ///
  /// CUSTOM METHODS
  ///

  def fail(): Flux[T] = wrapFlux[T](delegate.doOnNext(it => Assertions.fail()))
  def fail(message: String): Flux[T] = wrapFlux[T](delegate.doOnNext(_ => Assertions.fail(message)))
  def fail(cause: Throwable): Flux[T] = wrapFlux[T](delegate.doOnNext(_ => Assertions.fail(cause)))
  def failAndSupply(messageSupplier: () => String): Flux[T] = wrapFlux[T](delegate.doOnNext(_ => Assertions.fail(asJavaSupplier(messageSupplier))))
  def fail(message: String, cause: Throwable): Flux[T] = wrapFlux[T](delegate.doOnNext(_ => Assertions.fail(message, cause)))

  def assertTrue(predicate: T => Boolean): Flux[T] = wrapFlux[T](delegate.doOnNext(it => Assertions.assertTrue(predicate(it))))
  def assertTrue(predicate: T => Boolean, message: String): Flux[T] = wrapFlux[T](delegate.doOnNext(it => Assertions.assertTrue(predicate(it), message)))
  def assertTrue(predicate: T => Boolean, messageSupplier: () => String): Flux[T] = wrapFlux[T](delegate.doOnNext(it => Assertions.assertTrue(predicate(it), messageSupplier())))
  def assertTrue(predicate: T => Boolean, messageFn: T => String): Flux[T] = wrapFlux[T](delegate.doOnNext(it => Assertions.assertTrue(predicate(it), messageFn(it))))

  def assertFalse(predicate: T => Boolean): Flux[T] = wrapFlux[T](delegate.doOnNext(it => Assertions.assertFalse(predicate(it))))
  def assertFalse(predicate: T => Boolean, message: String): Flux[T] = wrapFlux[T](delegate.doOnNext(it => Assertions.assertFalse(predicate(it), message)))
  def assertFalse(predicate: T => Boolean, messageSupplier: () => String): Flux[T] = wrapFlux[T](delegate.doOnNext(it => Assertions.assertFalse(predicate(it), messageSupplier())))
  def assertFalse(predicate: T => Boolean, messageFn: T => String): Flux[T] = wrapFlux[T](delegate.doOnNext(it => Assertions.assertFalse(predicate(it), messageFn(it))))

  def assertEmpty(): Flux[T] = wrapFlux[T](delegate.doOnNext(it => Assertions.fail("An element was emitted despite assertEmpty() assertion")))
  def assertEmpty(message: String): Flux[T] = wrapFlux[T](delegate.doOnNext(it => Assertions.fail(message)))
  def assertEmpty(cause: Throwable): Flux[T] = wrapFlux[T](delegate.doOnNext(it => Assertions.fail("An element was emitted despite assertEmpty() assertion", cause)))
  def assertEmpty(message: String, cause: Throwable): Flux[T] = wrapFlux[T](delegate.doOnNext(it => Assertions.fail(message, cause)))

  def assertNotEmpty(): Flux[T] = wrapFlux[T](delegate.switchIfEmpty(Flux.error(new AssertionError("Flux was empty despite assertNotEmpty() assertion"))))
  def assertNotEmpty(message: String): Flux[T] = wrapFlux[T](delegate.switchIfEmpty(Flux.error(new AssertionError(message))))
  def assertNotEmpty(cause: Throwable): Flux[T] = wrapFlux[T](delegate.switchIfEmpty(Flux.error(new AssertionError("Flux was empty despite assertNotEmpty() assertion", cause))))
  def assertNotEmpty(message: String, cause: Throwable): Flux[T] = wrapFlux[T](delegate.switchIfEmpty(Flux.error(new AssertionError(message, cause))))

  def head(): Mono[T] = wrapMono(delegate.take(1).singleOrEmpty())
  def first(): Mono[T] = head()

  def min(ordering: Ordering[T]): Mono[T] = wrapMono(delegate.reduce(ordering.min))
  def max(ordering: Ordering[T]): Mono[T] = wrapMono(delegate.reduce(ordering.max))

  def isEmpty(): Mono[Boolean] = hasElements.map(value => !value)
  def isNonEmpty(): Mono[Boolean] = hasElements

  ///
  /// API METHODS
  ///

  def all(predicate: T => Boolean): Mono[Boolean] = wrapMono(delegate.all(asJavaPredicate(predicate))).asInstanceOf[Mono[Boolean]]
  def any(predicate: T => Boolean): Mono[Boolean] = wrapMono(delegate.any(asJavaPredicate(predicate))).asInstanceOf[Mono[Boolean]]

  def as[P](transformer: Flux[T] => P): P = delegate.as((jf: JFlux[T]) => transformer.apply(wrapFlux(jf)))

  def blockFirst(): Option[T] = Option(delegate.blockFirst())

  def blockFirst(timeout: Duration): Option[T] = timeout match {
    case _: Infinite => Option(delegate.blockFirst())
    case finiteDuration: FiniteDuration => Option(delegate.blockFirst(asJavaDuration(finiteDuration)))
  }

  def blockLast(): Option[T] = Option(delegate.blockLast())

  def blockLast(timeout: Duration): Option[T] = timeout match {
    case _: Infinite => Option(delegate.blockLast())
    case finiteDuration: FiniteDuration => Option(delegate.blockLast(asJavaDuration(finiteDuration)))
  }

  // todo benchmark toSeq!

  def buffer(): Flux[Seq[T]] = wrapFlux(delegate.buffer()).map(toScalaSeq)
  def buffer(maxSize: Int): Flux[Seq[T]] = wrapFlux(delegate.buffer(maxSize)).map(toScalaSeq)
  def buffer(maxSize: Int, skip: Int): Flux[Seq[T]] = wrapFlux(delegate.buffer(maxSize, skip)).map(toScalaSeq)
  def buffer(other: Publisher[T]): Flux[Seq[T]] = wrapFlux(delegate.buffer(other)).map(toScalaSeq)

  def buffer(bufferingTimespan: FiniteDuration): Flux[Seq[T]] = wrapFlux(delegate.buffer(asJavaDuration(bufferingTimespan))).map(toScalaSeq)
  def buffer(bufferingTimespan: FiniteDuration, openBufferEvery: FiniteDuration): Flux[Seq[T]] = wrapFlux(delegate.buffer(asJavaDuration(bufferingTimespan), asJavaDuration(openBufferEvery))).map(toScalaSeq)
  def buffer(bufferingTimespan: FiniteDuration, timer: Scheduler): Flux[Seq[T]] = wrapFlux(delegate.buffer(asJavaDuration(bufferingTimespan), timer)).map(toScalaSeq)
  def buffer(bufferingTimespan: FiniteDuration, openBufferEvery: FiniteDuration, timer: Scheduler): Flux[Seq[T]] = wrapFlux(delegate.buffer(asJavaDuration(bufferingTimespan), asJavaDuration(openBufferEvery), timer)).map(toScalaSeq)

//  def buffer[C >: mutable.Buffer[T]](maxSize: Int, bufferSupplier: () => C): Flux[Seq[T]] = {
//
////    val supplier: Supplier[C] = asJavaSupplier(() => toJavaCollection(bufferSupplier()))
//    val supplier: Supplier[C] = asJavaSupplier(() => toJavaList(bufferSupplier().asInstanceOf[mutable.Buffer[T]]))
//    val ms: Integer = maxSize
//    val value: JFlux[C] = delegate.buffer(maxSize.asInstanceOf[Integer], supplier)
//    wrapFlux(value)
//  }
//  def buffer[C <: mutable.Iterable[_]](maxSize: Int, skip: Int, bufferSupplier: () => C): Flux[C] = wrapFlux(delegate.buffer(maxSize, skip, asJavaSupplier(() => toJavaCollection(bufferSupplier()))))
//  def buffer[C <: mutable.Iterable[_]](other: Publisher[_], bufferSupplier: () => C): Flux[C] = wrapFlux(delegate.buffer(other, asJavaSupplier(() => toJavaCollection(bufferSupplier()))))

  def bufferTimeout(maxSize: Int, maxTime: FiniteDuration): Flux[Seq[T]] = wrapFlux(delegate.bufferTimeout(maxSize, asJavaDuration(maxTime))).map(toScalaSeq)
  // todo missing custom collection wrapper here (see JavaInterop's toIterable(collection: Collection) method)
  def bufferTimeout(maxSize: Int, maxTime: FiniteDuration, timer: Scheduler): Flux[Seq[T]] = wrapFlux(delegate.bufferTimeout(maxSize, asJavaDuration(maxTime), timer)).map(toScalaSeq)
  // todo missing custom collection wrapper here

  def bufferUntil(predicate: T => Boolean): Flux[Seq[T]] = wrapFlux(delegate.bufferUntil(asJavaPredicate(predicate))).map(toScalaSeq)
  def bufferUntil(predicate: T => Boolean, cutBefore: Boolean): Flux[Seq[T]] = wrapFlux(delegate.bufferUntil(asJavaPredicate(predicate), cutBefore)).map(toScalaSeq)

  def bufferWhile(predicate: T => Boolean): Flux[Seq[T]] = wrapFlux(delegate.bufferWhile(asJavaPredicate(predicate))).map(toScalaSeq)

  def bufferWhen[U, V](bucketOpening: Publisher[U], closeSelector: U => Publisher[V]): Flux[Seq[T]] = wrapFlux(delegate.bufferWhen(bucketOpening, asJavaFn1(closeSelector))).map(toScalaSeq)
  // todo missing custom collection wrapper here (see JavaConverters.asJavaCollection())

  def cache(): Flux[T] = wrapFlux[T](delegate.cache())
  def cache(history: Int): Flux[T] = wrapFlux[T](delegate.cache(history))
  def cache(ttl: Duration): Flux[T] = ttl match {
    case _: Infinite => cache()
    case finiteDuration: FiniteDuration => wrapFlux(delegate.cache(asJavaDuration(finiteDuration)))
  }
  def cache(ttl: Duration, timer: Scheduler): Flux[T] = ttl match {
    case _: Infinite => cache()
    case finiteDuration: FiniteDuration => wrapFlux(delegate.cache(asJavaDuration(finiteDuration), timer))
  }
  def cache(history: Int, ttl: Duration): Flux[T] = ttl match {
    case _: Infinite => cache(history)
    case finiteDuration: FiniteDuration => wrapFlux(delegate.cache(history, asJavaDuration(finiteDuration)))
  }
  def cache(history: Int, ttl: Duration, timer: Scheduler): Flux[T] = ttl match {
    case _: Infinite => cache(history)
    case finiteDuration: FiniteDuration => wrapFlux(delegate.cache(history, asJavaDuration(finiteDuration), timer))
  }

  def cast[E](clazz: Class[E]): Flux[E] = wrapFlux[E](delegate.cast(clazz))

  def cancelOn(scheduler: Scheduler): Flux[T] = wrapFlux[T](delegate.cancelOn(scheduler))

  def checkpoint(): Flux[T] = wrapFlux[T](delegate.checkpoint())
  def checkpoint(description: String): Flux[T] = wrapFlux[T](delegate.checkpoint(description))
  def checkpoint(description: Option[String], forceStackTrace: Boolean): Flux[T] = description match {
    case Some(desc) => wrapFlux(delegate.checkpoint(desc, forceStackTrace))
    case None => wrapFlux(delegate.checkpoint(null, forceStackTrace)) // this java api accepts Nullable
  }

  def collect[E](containerSupplier: () => E, collector: (E, T) => Unit): Mono[E] = wrapMono(delegate.collect(asJavaSupplier(containerSupplier), asJavaBiConsumer(collector)))
  def collect[R, A](collector: Collector[T, A, R]): Mono[R] = wrapMono(delegate.collect(collector))
  def collectSeq(): Mono[Seq[T]] = wrapMono(delegate.collectList()).map(toScalaSeq)

  def collectMap[K](keyExtractor: T => K): Mono[Map[K, T]] = {
    // this explicit type def is required
    val mono: Mono[util.Map[K, T]] = wrapMono(delegate.collectMap(asJavaFn1(keyExtractor)))
    mono.map(toScalaMap)
  }

  def collectMap[K, V](keyExtractor: T => K, valueExtractor: T => V): Mono[Map[K, V]] = {
    // this explicit type def is required
    val mono: Mono[util.Map[K, V]] = wrapMono(delegate.collectMap(asJavaFn1(keyExtractor), asJavaFn1(valueExtractor)))
    mono.map(toScalaMap)
  }

  def collectMap[K, V](keyExtractor: T => K, valueExtractor: T => V, mapSupplier: () => mutable.Map[K, V]): Mono[Map[K, V]] = {
    // this explicit type def is required
    val mono: Mono[util.Map[K, V]] = wrapMono(delegate.collectMap(asJavaFn1(keyExtractor), asJavaFn1(valueExtractor), () => toJavaMutableMap(mapSupplier())))
    mono.map(toScalaMap)
  }

  def collectMultimap[K](keyExtractor: T => K): Mono[Map[K, Seq[T]]] = {
    // this explicit type def is required
    val mono: Mono[util.Map[K, util.Collection[T]]] = wrapMono(delegate.collectMultimap(asJavaFn1(keyExtractor)))
    mono.map(toScalaMap).map(map => map.mapValues(toScalaSeq))
  }

  def collectMultimap[K, V](keyExtractor: T => K, valueExtractor: T => V): Mono[Map[K, Seq[V]]] = {
    // this explicit type def is required
    val mono: Mono[util.Map[K, util.Collection[V]]] = wrapMono(delegate.collectMultimap(asJavaFn1(keyExtractor), asJavaFn1(valueExtractor)))
    mono.map(toScalaMap).map(map => map.mapValues(toScalaSeq))
  }

  // todo find out if this can be implemented without copying the map, since the intent is to have the map be mutable
  //   so the mapSupplier's map's implementation can be used
//  def collectMultimap[K, V](keyExtractor: T => K, valueExtractor: T => V, mapSupplier: () => mutable.Map[K, mutable.Iterable[V]]): Mono[_ <: Map[K, Iterable[V]]] = {
//    val supplier: Supplier[util.Map[K, util.Collection[V]]] = () => {
//      val smap: mutable.Map[K, mutable.Iterable[V]] = mapSupplier()
//      val map: java.util.Map[K, util.Collection[V]] = asJavaMutableMap(mutable.Map())
//      smap.keys.forEach((k: K) => map.put(k, asJavaCollection(smap(k))))
//      map
//    }
//
//    // this explicit type def is required
//    val mono: Mono[util.Map[K, util.Collection[V]]] = wrapMono(delegate.collectMultimap(keyExtractor, valueExtractor, supplier))
//    mono.map(toMap).map(map => map.mapValues(toIterable))
//  }

  def collectSortedList(): Mono[Seq[T]] = wrapMono(delegate.collectSortedList().map(toScalaSeq(_)))
  def collectSortedList(ordering: Ordering[T]): Mono[Seq[T]] = wrapMono(delegate.collectSortedList(ordering).map(toScalaSeq(_)))

  // will be removed 3.4
//  def compose[V](transformer: Flux[T] => Publisher[V]): Flux[V] = wrapFlux[V](delegate.compose(transformer))

  def concatMap[V](mapper: T => Publisher[V]): Flux[V] = wrapFlux[V](delegate.concatMap(asJavaFn1(mapper)))
  def concatMap[V](mapper: T => Publisher[V], prefetch: Int): Flux[V] = wrapFlux[V](delegate.concatMap(asJavaFn1(mapper), prefetch))

  def concatMapDelayError[V](mapper: T => Publisher[V]): Flux[V] = wrapFlux[V](delegate.concatMapDelayError(asJavaFn1(mapper)))
  def concatMapDelayError[V](mapper: T => Publisher[V], prefetch: Int): Flux[V] = wrapFlux[V](delegate.concatMapDelayError(asJavaFn1(mapper), prefetch))
  def concatMapDelayError[V](mapper: T => Publisher[V], delayUntilEnd: Boolean, prefetch: Int): Flux[V] = wrapFlux[V](delegate.concatMapDelayError(asJavaFn1(mapper), delayUntilEnd, prefetch))

  def concatMapIterable[R](mapper: T => Iterable[R]): Flux[R] = {
    val m: util.function.Function[_ >: T, _ <: lang.Iterable[R]] = (t: T) => asJavaIterable(mapper(t))
    wrapFlux(delegate.concatMapIterable(m))
  }
  def concatMapIterable[R](mapper: T => Iterable[R], prefetch: Int): Flux[R] = {
    val m: util.function.Function[_ >: T, _ <: lang.Iterable[R]] = (t: T) => asJavaIterable(mapper(t))
    wrapFlux(delegate.concatMapIterable(m, prefetch))
  }

  def concatWith(other: Publisher[T]): Flux[T] = wrapFlux[T](delegate.concatWith(other))

  def count(): Mono[Long] = wrapMono(delegate.count().map(Long2long))

  def defaultIfEmpty(defaultV: T): Flux[T] = wrapFlux[T](delegate.defaultIfEmpty(defaultV))

  def delayElements(delay: FiniteDuration): Flux[T] = wrapFlux[T](delegate.delayElements(asJavaDuration(delay)))
  def delayElements(delay: FiniteDuration, timer: Scheduler): Flux[T] = wrapFlux[T](delegate.delayElements(asJavaDuration(delay), timer))

  def delaySequence(delay: FiniteDuration): Flux[T] = wrapFlux[T](delegate.delaySequence(asJavaDuration(delay)))
  def delaySequence(delay: FiniteDuration, timer: Scheduler): Flux[T] = wrapFlux[T](delegate.delaySequence(asJavaDuration(delay), timer))

  def delayUntil(triggerProvider: T => Publisher[_]): Flux[T] = wrapFlux[T](delegate.delayUntil(asJavaFn1(triggerProvider)))

  def delaySubscription(delay: FiniteDuration): Flux[T] = wrapFlux[T](delegate.delaySubscription(asJavaDuration(delay)))
  def delaySubscription(delay: FiniteDuration, timer: Scheduler): Flux[T] = wrapFlux[T](delegate.delaySubscription(asJavaDuration(delay), timer))
  def delaySubscription[U](subscriptionDelay: Publisher[U]): Flux[T] = wrapFlux[T](delegate.delaySubscription(subscriptionDelay))

  def dematerialize[X](): Flux[X] = wrapFlux[X](delegate.dematerialize())

  def distinct(): Flux[T] = wrapFlux[T](delegate.distinct())
  def distinct[V](keySelector: T => V): Flux[T] = wrapFlux[T](delegate.distinct[V](asJavaFn1(keySelector)))

  // todo: missing two mutable supplied collection using methods. Should try to include?

  def distinctUntilChanged(): Flux[T] = wrapFlux[T](delegate.distinctUntilChanged())
  def distinctUntilChanged[V](keySelector: T => V): Flux[T] = wrapFlux[T](delegate.distinctUntilChanged[V](asJavaFn1(keySelector)))
  def distinctUntilChanged[V](keySelector: T => V, keyComparator: (V, V) => Boolean): Flux[T] = wrapFlux[T](delegate.distinctUntilChanged[V](asJavaFn1(keySelector), asJavaBiPredicate(keyComparator)))

  def doAfterTerminate(afterTerminate: () => Unit): Flux[T] = wrapFlux[T](delegate.doAfterTerminate(asJavaRunnable(afterTerminate)))

  def doOnCancel(onCancel: () => Unit): Flux[T] = wrapFlux[T](delegate.doOnCancel(asJavaRunnable(onCancel)))
  def doOnComplete(onComplete: () => Unit): Flux[T] = wrapFlux[T](delegate.doOnComplete(asJavaRunnable(onComplete)))
  def doOnDiscard[R](classType: Class[R], discardHook: R => Unit): Flux[T] = wrapFlux[T](delegate.doOnDiscard(classType, asJavaConsumer(discardHook)))
  def doOnEach(signalConsumer: Signal[T] => Unit): Flux[T] = wrapFlux[T](delegate.doOnEach(asJavaConsumer(signalConsumer)))
  def doOnError(onError: Throwable => Unit): Flux[T] = wrapFlux[T](delegate.doOnError(asJavaConsumer(onError)))
  def doOnError[E <: Throwable](exceptionType: Class[E], onError: Throwable => Unit): Flux[T] = wrapFlux[T](delegate.doOnError[E](exceptionType, asJavaConsumer(onError)))
  def doOnError(predicate: Throwable => Boolean, onError: Throwable => Unit): Flux[T] = wrapFlux[T](delegate.doOnError(asJavaPredicate(predicate), asJavaConsumer(onError)))
  def doOnNext(onNext: T => Unit): Flux[T] = wrapFlux[T](delegate.doOnNext(asJavaConsumer(onNext)))
  def doOnRequest(consumer: Long => Unit): Flux[T] = wrapFlux[T](delegate.doOnRequest(asJavaLongConsumer(consumer)))
  def doOnSubscribe(onSubscribe: Subscription => Unit): Flux[T] = wrapFlux[T](delegate.doOnSubscribe(asJavaConsumer(onSubscribe)))
  def doOnTerminate(onTerminate: () => Unit): Flux[T] = wrapFlux[T](delegate.doOnTerminate(asJavaRunnable(onTerminate)))
  def doFirst(onFirst: () => Unit): Flux[T] = wrapFlux[T](delegate.doFirst(asJavaRunnable(onFirst)))
  def doFinally(onFinally: SignalType => Unit): Flux[T] = wrapFlux[T](delegate.doFinally(asJavaConsumer(onFinally)))

  def elapsed(): Flux[(Long, T)] = wrapFlux[JTuple2[java.lang.Long, T]](delegate.elapsed()).map(tuple => (Long2long(toScalaTuple2(tuple)._1), toScalaTuple2(tuple)._2))
  def elapsed(scheduler: Scheduler): Flux[(Long, T)] = wrapFlux[JTuple2[java.lang.Long, T]](delegate.elapsed()).map(tuple => toScalaTuple2(tuple.mapT1(t1 => Long2long(t1))))

  def elementAt(index: Int): Mono[T] = wrapMono[T](delegate.elementAt(index))
  def elementAt(index: Int, defaultValue: T): Mono[T] = wrapMono[T](delegate.elementAt(index, defaultValue))

  def expandDeep(expander: T => Publisher[T]): Flux[T] = wrapFlux[T](delegate.expandDeep(asJavaFn1(expander)))
  def expandDeep(expander: T => Publisher[T], capacityHint: Int): Flux[T] = wrapFlux[T](delegate.expandDeep(asJavaFn1(expander), capacityHint))

  def expand(expander: T => Publisher[T]): Flux[T] = wrapFlux[T](delegate.expand(asJavaFn1(expander)))
  def expand(expander: T => Publisher[T], capacityHint: Int): Flux[T] = wrapFlux[T](delegate.expand(asJavaFn1(expander), capacityHint))

  def filter(predicate: T => Boolean): Flux[T] = wrapFlux[T](delegate.filter(asJavaPredicate(predicate)))

  def filterWhen(asyncPredicate: T => Publisher[Boolean]): Flux[T] = wrapFlux[T](delegate.filterWhen((t: T) => Flux.from(asyncPredicate(t)).map(boolean2Boolean)))
  def filterWhen(asyncPredicate: T => Publisher[Boolean], bufferSize: Int): Flux[T] = wrapFlux[T](delegate.filterWhen((t: T) => Flux.from(asyncPredicate(t)).map(boolean2Boolean), bufferSize))

  def flatMap[R](mapper: T => Publisher[R]): Flux[R] = wrapFlux[R](delegate.flatMap(asJavaFn1(mapper)))
  def flatMap[V](mapper: T => Publisher[V], concurrency: Int): Flux[V] = wrapFlux[V](delegate.flatMap(asJavaFn1(mapper), concurrency))
  def flatMap[V](mapper: T => Publisher[V], concurrency: Int, prefetch: Int): Flux[V] = wrapFlux[V](delegate.flatMap(asJavaFn1(mapper), concurrency, prefetch))
  def flatMap[R](mapperOnNext: T => Publisher[R], mapperOnError: Throwable => Publisher[R], mapperOnComplete: () => Publisher[R]): Flux[R] = wrapFlux[R](delegate.flatMap(asJavaFn1(mapperOnNext), asJavaFn1(mapperOnError), asJavaSupplier(mapperOnComplete)))
  def flatMapDelayError[V](mapper: T => Publisher[V], concurrency: Int, prefetch: Int): Flux[V] = wrapFlux[V](delegate.flatMapDelayError(asJavaFn1(mapper), concurrency, prefetch))

  def flatMapIterable[R](mapper: T => Iterable[R]): Flux[R] = wrapFlux[R](delegate.flatMapIterable((t: T) => asJavaIterable(mapper(t))))
  def flatMapIterable[R](mapper: T => Iterable[R], prefetch: Int): Flux[R] = wrapFlux[R](delegate.flatMapIterable((t: T) => asJavaIterable(mapper(t)), prefetch))

  def flatMapSequential[R](mapper: T => Publisher[R]): Flux[R] = wrapFlux[R](delegate.flatMapSequential(asJavaFn1(mapper)))
  def flatMapSequential[R](mapper: T => Publisher[R], maxConcurrency: Int): Flux[R] = wrapFlux[R](delegate.flatMapSequential(asJavaFn1(mapper), maxConcurrency))
  def flatMapSequential[R](mapper: T => Publisher[R], maxConcurrency: Int, prefetch: Int): Flux[R] = wrapFlux[R](delegate.flatMapSequential(asJavaFn1(mapper), maxConcurrency, prefetch))
  def flatMapSequentialDelayError[R](mapper: T => Publisher[R], maxConcurrency: Int, prefetch: Int): Flux[R] = wrapFlux[R](delegate.flatMapSequentialDelayError(asJavaFn1(mapper), maxConcurrency, prefetch))

  def getPrefetch: Int = delegate.getPrefetch

  def groupBy[K](keyMapper: T => K): Flux[GroupedFlux[K, T]] = wrapFlux[GroupedFlux[K, T]](delegate.groupBy[K](asJavaFn1(keyMapper)).map(wrapGroupedFlux[K, T](_)))
  def groupBy[K](keyMapper: T => K, prefetch: Int): Flux[GroupedFlux[K, T]] = wrapFlux[GroupedFlux[K, T]](delegate.groupBy[K](asJavaFn1(keyMapper), prefetch).map(wrapGroupedFlux[K, T](_)))
  def groupBy[K, V](keyMapper: T => K, valueMapper: T => V): Flux[GroupedFlux[K, V]] = wrapFlux[GroupedFlux[K, V]](delegate.groupBy[K, V](asJavaFn1(keyMapper), asJavaFn1(valueMapper)).map(wrapGroupedFlux[K, V](_)))
  def groupBy[K, V](keyMapper: T => K, valueMapper: T => V, prefetch: Int): Flux[GroupedFlux[K, V]] = wrapFlux[GroupedFlux[K, V]](delegate.groupBy[K, V](asJavaFn1(keyMapper), asJavaFn1(valueMapper), prefetch).map(wrapGroupedFlux[K, V](_)))

  def groupJoin[TRight, TLeftEnd, TRightEnd, R](other: Publisher[TRight], leftEnd: T => Publisher[TLeftEnd], rightEnd: TRight => Publisher[TRightEnd], resultSelector: (T, Flux[TRight]) => R): Flux[R] = wrapFlux[R](delegate.groupJoin(other, asJavaFn1(leftEnd), asJavaFn1(rightEnd), (t: T, f: JFlux[TRight]) => resultSelector(t, wrapFlux(f))))
  // commented out code is equivalent to the above function
//  def groupJoin[TRight, TLeftEnd, TRightEnd, R](other: Publisher[TRight], leftEnd: T => Publisher[TLeftEnd], rightEnd: TRight => Publisher[TRightEnd], resultSelector: (T, Flux[TRight]) => R): Flux[R] = {
//    val resultSelectorJFlux: java.util.function.BiFunction[T, JFlux[TRight], R] = (t: T, f: JFlux[TRight]) => resultSelector.apply(t, wrapFlux[TRight](f))
//    wrapFlux[R](delegate.groupJoin[TRight, TLeftEnd, TRightEnd, R](other, leftEnd, rightEnd, resultSelectorJFlux))
//  }

  def handle[R](handler: (T, SynchronousSink[R]) => Unit): Flux[R] = wrapFlux[R](delegate.handle(asJavaBiConsumer(handler)))

  def hasElement(value: T): Mono[Boolean] = wrapMono[Boolean](delegate.hasElement(value).map(Boolean2boolean))

  def hasElements: Mono[Boolean] = wrapMono[Boolean](delegate.hasElements.map(Boolean2boolean))

  def hide(): Flux[T] = wrapFlux[T](delegate.hide())

  def index(): Flux[(Long, T)] = wrapFlux[(Long, T)](delegate.index().map(tuple => toScalaTuple2(tuple.mapT1(t1 => Long2long(t1)))))
  def index[I](indexMapper: (Long, T) => I): Flux[I] = wrapFlux[I](delegate.index((l: java.lang.Long, t: T) => indexMapper(Long2long(l), t)))

  def ignoreElements(): Mono[T] = wrapMono[T](delegate.ignoreElements())

  def join[TRight, TLeftEnd, TRightEnd, R](other: Publisher[TRight], leftEnd: T => Publisher[TLeftEnd], rightEnd: TRight => Publisher[TRightEnd], resultSelector: (T, TRight) => R): Flux[R] = wrapFlux[R](delegate.join(other, asJavaFn1(leftEnd), asJavaFn1(rightEnd), asJavaFn2(resultSelector)))

  def last(): Mono[T] = wrapMono[T](delegate.last())
  def last(defaultValue: T): Mono[T] = wrapMono[T](delegate.last(defaultValue))

  def limitRate(prefetchRate: Int): Flux[T] = wrapFlux[T](delegate.limitRate(prefetchRate))
  def limitRate(highTide: Int, lowTide: Int): Flux[T] = wrapFlux[T](delegate.limitRate(highTide, lowTide))
  def limitRequest(requestCap: Long): Flux[T] = wrapFlux[T](delegate.limitRequest(requestCap))

  def log(): Flux[T] = wrapFlux[T](delegate.log())
  def log(category: String): Flux[T] = wrapFlux[T](delegate.log(category))
  def log(level: Level): Flux[T] = log(null, level)
  def log(category: String, level: Level): Flux[T] = wrapFlux[T](delegate.log(category, level))
  def log(category: String, level: Level, options: SignalType*): Flux[T] = wrapFlux[T](delegate.log(category, level, options:_*))
  def log(level: Level, showOperatorLine: Boolean, options: SignalType*): Flux[T] = {
    val nullString: String = null // null needs a type to disambiguate method calls
    log(nullString, level, showOperatorLine, options:_*)
  }
  def log(category: String, level: Level, showOperatorLine: Boolean, options: SignalType*): Flux[T] = wrapFlux[T](delegate.log(category, level, showOperatorLine, options:_*))
  def log(logger: Logger): Flux[T] = wrapFlux[T](delegate.log(logger))
  def log(logger: Logger, level: Level, showOperatorLine: Boolean, options: SignalType*): Flux[T] = wrapFlux[T](delegate.log(logger, level, showOperatorLine, options:_*))

  def map[V](mapper: T => V): Flux[V] = wrapFlux[V](delegate.map(asJavaFn1(mapper)))

  def materialize(): Flux[Signal[T]] = wrapFlux[Signal[T]](delegate.materialize())

  def mergeOrderedWith(other: Publisher[T], ordering: Ordering[T]): Flux[T] = wrapFlux[T](delegate.mergeOrderedWith(other, ordering))
  def mergeWith(other: Publisher[T]): Flux[T] = wrapFlux[T](delegate.mergeWith(other))

  def metrics(): Flux[T] = wrapFlux[T](delegate.metrics())

  def name(name: String): Flux[T] = wrapFlux[T](delegate.name(name))

  def next(): Mono[T] = wrapMono[T](delegate.next())

  def ofType[U](clazz: Class[U]): Flux[U] = wrapFlux[U](delegate.ofType[U](clazz))

  def onBackpressureBuffer(): Flux[T] = wrapFlux[T](delegate.onBackpressureBuffer())
  def onBackpressureBuffer(maxSize: Int): Flux[T] = wrapFlux[T](delegate.onBackpressureBuffer(maxSize))
  def onBackpressureBuffer(maxSize: Int, onOverflow: T => Unit): Flux[T] = wrapFlux[T](delegate.onBackpressureBuffer(maxSize, asJavaConsumer(onOverflow)))
  def onBackpressureBuffer(maxSize: Int, bufferOverflowStrategy: BufferOverflowStrategy): Flux[T] = wrapFlux[T](delegate.onBackpressureBuffer(maxSize, bufferOverflowStrategy))
  def onBackpressureBuffer(maxSize: Int, onOverflow: T => Unit, bufferOverflowStrategy: BufferOverflowStrategy): Flux[T] = wrapFlux[T](delegate.onBackpressureBuffer(maxSize, asJavaConsumer(onOverflow), bufferOverflowStrategy))
  def onBackpressureBuffer(ttl: FiniteDuration, maxSize: Int, onBufferEviction: T => Unit): Flux[T] = wrapFlux[T](delegate.onBackpressureBuffer(asJavaDuration(ttl), maxSize, asJavaConsumer(onBufferEviction)))
  def onBackpressureBuffer(ttl: FiniteDuration, maxSize: Int, onBufferEviction: T => Unit, scheduler: Scheduler): Flux[T] = wrapFlux[T](delegate.onBackpressureBuffer(asJavaDuration(ttl), maxSize, asJavaConsumer(onBufferEviction), scheduler))

  def onBackpressureDrop(): Flux[T] = wrapFlux[T](delegate.onBackpressureDrop())
  def onBackpressureDrop(onDropped: T => Unit): Flux[T] = wrapFlux[T](delegate.onBackpressureDrop(asJavaConsumer(onDropped)))

  def onBackpressureError(): Flux[T] = wrapFlux[T](delegate.onBackpressureError())

  def onBackpressureLatest(): Flux[T] = wrapFlux[T](delegate.onBackpressureLatest())

  def onErrorContinue(errorConsumer: (Throwable, AnyRef) => Unit): Flux[T] = wrapFlux[T](delegate.onErrorContinue(asJavaBiConsumer(errorConsumer)))
  def onErrorContinue[E <: Throwable](classType: Class[E], errorConsumer: (Throwable, AnyRef) => Unit): Flux[T] = wrapFlux[T](delegate.onErrorContinue[E](classType, asJavaBiConsumer(errorConsumer)))
  def onErrorContinue[E <: Throwable](errorPredicate: E => Boolean, errorConsumer: (Throwable, AnyRef) => Unit): Flux[T] = wrapFlux[T](delegate.onErrorContinue[E](asJavaPredicate[E](errorPredicate), asJavaBiConsumer[Throwable, AnyRef](errorConsumer)))

  def onErrorStop(): Flux[T] = wrapFlux[T](delegate.onErrorStop())

  def onErrorMap(mapper: Throwable => Throwable): Flux[T] = wrapFlux[T](delegate.onErrorMap(asJavaFn1(mapper)))
  def onErrorMap[E <: Throwable](classType: Class[E], mapper: E => Throwable): Flux[T] = wrapFlux[T](delegate.onErrorMap(classType, asJavaFn1(mapper)))
  def onErrorMap(predicate: Throwable => Boolean, mapper: Throwable => Throwable): Flux[T] = wrapFlux[T](delegate.onErrorMap(asJavaFn1(mapper)))

  def onErrorResume(fallback: Throwable => Publisher[T]): Flux[T] = wrapFlux[T](delegate.onErrorResume(asJavaFn1(fallback)))
  def onErrorResume[E <: Throwable](classType: Class[E], fallback: E => Publisher[T]): Flux[T] = wrapFlux[T](delegate.onErrorResume(classType, asJavaFn1(fallback)))
  def onErrorResume(predicate: Throwable => Boolean, fallback: Throwable => Publisher[T]): Flux[T] = wrapFlux[T](delegate.onErrorResume(asJavaPredicate(predicate), asJavaFn1(fallback)))

  def onErrorReturn(fallbackValue: T): Flux[T] = wrapFlux[T](delegate.onErrorReturn(fallbackValue))
  def onErrorReturn[E <: Throwable](classType: Class[E], fallbackValue: T): Flux[T] = wrapFlux[T](delegate.onErrorReturn(classType, fallbackValue))
  def onErrorReturn(predicate: Throwable => Boolean, fallbackValue: T): Flux[T] = wrapFlux[T](delegate.onErrorReturn(asJavaPredicate(predicate), fallbackValue))

  def onTerminateDetach(): Flux[T] = wrapFlux[T](delegate.onTerminateDetach())

  def or(other: Publisher[T]): Flux[T] = wrapFlux[T](delegate.or(other))

  // todo: uncomment these methods after creating a scala ParallelFlux wrapper
  def parallel(): ParallelFlux[T] = wrapParallelFlux[T](delegate.parallel())
  def parallel(parallelism: Int): ParallelFlux[T] = wrapParallelFlux[T](delegate.parallel(parallelism))
  def parallel(parallelism: Int, prefetch: Int): ParallelFlux[T] = wrapParallelFlux[T](delegate.parallel(parallelism, prefetch))

  def publish(): ConnectableFlux[T] = wrapConnectableFlux[T](delegate.publish())
  def publish(prefetch: Int): ConnectableFlux[T] = wrapConnectableFlux[T](delegate.publish(prefetch))
  def publish[R](transform: Flux[T] => Publisher[R]): Flux[R] = wrapFlux[R](delegate.publish[R](asJavaFn1((jflux: JFlux[T]) => transform(wrapFlux(jflux)))))
  def publish[R](transform: Flux[T] => Publisher[R], prefetch: Int): Flux[R] = wrapFlux[R](delegate.publish[R](asJavaFn1((jflux: JFlux[T]) => transform(wrapFlux(jflux))), prefetch))

  def publishNext(): Mono[T] = wrapMono[T](delegate.publishNext())

  def publishOn(scheduler: Scheduler): Flux[T] = wrapFlux[T](delegate.publishOn(scheduler))
  def publishOn(scheduler: Scheduler, prefetch: Int): Flux[T] = wrapFlux[T](delegate.publishOn(scheduler, prefetch))
  def publishOn(scheduler: Scheduler, delayError: Boolean, prefetch: Int): Flux[T] = wrapFlux[T](delegate.publishOn(scheduler, delayError, prefetch))

  def reduce(aggregator: (T, T) => T): Mono[T] = wrapMono[T](delegate.reduce(asJavaFn2(aggregator)))
  def reduce[A](initial: A, accumulator: (A, T) => A): Mono[A] = wrapMono[A](delegate.reduce[A](initial, asJavaFn2(accumulator)))
  def reduceWith[A](initial: () => A, accumulator: (A, T) => A): Mono[A] = wrapMono[A](delegate.reduceWith[A](asJavaSupplier(initial), asJavaFn2(accumulator)))

  def repeat(): Flux[T] = wrapFlux[T](delegate.repeat())
  def repeat(predicate: () => Boolean): Flux[T] = wrapFlux[T](delegate.repeat(asJavaBooleanSupplier(predicate)))
  def repeat(numRepeat: Long): Flux[T] = wrapFlux[T](delegate.repeat(numRepeat))
  def repeat(numRepeat: Long, predicate: () => Boolean): Flux[T] = wrapFlux[T](delegate.repeat(numRepeat, asJavaBooleanSupplier(predicate)))
  def repeatWhen(repeatFactory: Flux[Long] => Publisher[_]): Flux[T] = wrapFlux[T](delegate.repeatWhen((flux: JFlux[lang.Long]) => wrapFlux(flux).map(Long2long)))

  def replay(): ConnectableFlux[T] = wrapConnectableFlux[T](delegate.replay())
  def replay(history: Int): ConnectableFlux[T] = wrapConnectableFlux[T](delegate.replay(history))
  def replay(ttl: FiniteDuration): ConnectableFlux[T] = wrapConnectableFlux[T](delegate.replay(asJavaDuration(ttl)))
  def replay(history: Int, ttl: FiniteDuration): ConnectableFlux[T] = wrapConnectableFlux[T](delegate.replay(history, asJavaDuration(ttl)))
  def replay(ttl: FiniteDuration, timer: Scheduler): ConnectableFlux[T] = wrapConnectableFlux[T](delegate.replay(asJavaDuration(ttl), timer))
  def replay(history: Int, ttl: FiniteDuration, timer: Scheduler): ConnectableFlux[T] = wrapConnectableFlux[T](delegate.replay(history, asJavaDuration(ttl), timer))

  def retry(): Flux[T] = wrapFlux[T](delegate.retry())
  def retry(numRetries: Long): Flux[T] = wrapFlux[T](delegate.retry(numRetries))
  def retry(retryMatcher: Throwable => Boolean): Flux[T] = wrapFlux[T](delegate.retry(asJavaPredicate(retryMatcher)))
  def retry(numRetries: Long, retryMatcher: Throwable => Boolean): Flux[T] = wrapFlux[T](delegate.retry(numRetries, asJavaPredicate(retryMatcher)))
  def retryWhen(whenFactory: Flux[Throwable] => Publisher[_]): Flux[T] = wrapFlux[T](delegate.retryWhen((flux: JFlux[Throwable]) => whenFactory(wrapFlux(flux))))

  def retryBackoff(numRetries: Long, firstBackoff: FiniteDuration): Flux[T] = wrapFlux[T](delegate.retryBackoff(numRetries, asJavaDuration(firstBackoff)))
  def retryBackoff(numRetries: Long, firstBackoff: FiniteDuration, maxBackoff: FiniteDuration): Flux[T] = wrapFlux[T](delegate.retryBackoff(numRetries, asJavaDuration(firstBackoff), asJavaDuration(maxBackoff)))
  def retryBackoff(numRetries: Long, firstBackoff: FiniteDuration, maxBackoff: FiniteDuration, backoffScheduler: Scheduler): Flux[T] = wrapFlux[T](delegate.retryBackoff(numRetries, asJavaDuration(firstBackoff), asJavaDuration(maxBackoff), backoffScheduler))
  def retryBackoff(numRetries: Long, firstBackoff: FiniteDuration, maxBackoff: FiniteDuration, jitterFactor: Double): Flux[T] = wrapFlux[T](delegate.retryBackoff(numRetries, asJavaDuration(firstBackoff), asJavaDuration(maxBackoff), jitterFactor))
  def retryBackoff(numRetries: Long, firstBackoff: FiniteDuration, maxBackoff: FiniteDuration, jitterFactor: Double, backoffScheduler: Scheduler): Flux[T] = wrapFlux[T](delegate.retryBackoff(numRetries, asJavaDuration(firstBackoff), asJavaDuration(maxBackoff), jitterFactor, backoffScheduler))

  def sample(timespan: FiniteDuration): Flux[T] = wrapFlux[T](delegate.sample(asJavaDuration(timespan)))
  def sample[U](sampler: Publisher[U]): Flux[T] = wrapFlux[T](delegate.sample(sampler))

  def sampleFirst(timespan: FiniteDuration): Flux[T] = wrapFlux[T](delegate.sampleFirst(asJavaDuration(timespan)))
  def sampleFirst[U](samplerFactory: T => Publisher[U]): Flux[T] = wrapFlux[T](delegate.sampleFirst(asJavaFn1(samplerFactory)))

  def sampleTimeout[U](throttlerFactory: T => Publisher[U]): Flux[T] = wrapFlux[T](delegate.sampleTimeout(asJavaFn1(throttlerFactory)))
  def sampleTimeout[U](throttlerFactory: T => Publisher[U], maxConcurrency: Int): Flux[T] = wrapFlux[T](delegate.sampleTimeout(asJavaFn1(throttlerFactory), maxConcurrency))

  def scan(accumulator: (T, T) => T): Flux[T] = wrapFlux[T](delegate.scan(asJavaFn2(accumulator)))
  def scan[A](initial: A, accumulator: (A, T) => A): Flux[A] = wrapFlux[A](delegate.scan(initial, asJavaFn2(accumulator)))
  def scanWith[A](initial: () => A, accumulator: (A, T) => A): Flux[A] = wrapFlux[A](delegate.scanWith(asJavaSupplier(initial), asJavaFn2(accumulator)))

  def share(): Flux[T] = wrapFlux[T](delegate.share())

  def single(): Mono[T] = wrapMono[T](delegate.single())
  def single(defaultValue: T): Mono[T] = wrapMono[T](delegate.single(defaultValue))
  def singleOrEmpty(): Mono[T] = wrapMono[T](delegate.singleOrEmpty())

  def skip(skipped: Long): Flux[T] = wrapFlux[T](delegate.skip(skipped))
  def skip(timespan: FiniteDuration): Flux[T] = wrapFlux[T](delegate.skip(asJavaDuration(timespan)))
  def skip(timespan: FiniteDuration, timer: Scheduler): Flux[T] = wrapFlux[T](delegate.skip(asJavaDuration(timespan), timer))
  def skipLast(n: Int): Flux[T] = wrapFlux[T](delegate.skipLast(n))
  def skipUntil(untilPredicate: T => Boolean): Flux[T] = wrapFlux[T](delegate.skipUntil(asJavaPredicate(untilPredicate)))
  def skipUntilOther(other: Publisher[_]): Flux[T] = wrapFlux[T](delegate.skipUntilOther(other))
  def skipWhile(skipPredicate: T => Boolean): Flux[T] = wrapFlux[T](delegate.skipWhile(asJavaPredicate(skipPredicate)))

  def sort(): Flux[T] = wrapFlux[T](delegate.sort())
  def sort(ordering: Ordering[T]): Flux[T] = wrapFlux[T](delegate.sort(ordering))

  def startWith(iterable: Iterable[T]): Flux[T] = wrapFlux[T](delegate.startWith(asJavaIterable(iterable)))
  def startWith(values: T*): Flux[T] = wrapFlux[T](delegate.startWith(values:_*))
  def startWith(publisher: Publisher[T]): Flux[T] = wrapFlux[T](delegate.startWith(publisher))

  // todo add optional alternatives or extra overloads to subscribe(...)? note these are nullable values in public api
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

  def subscriberContext(mergeContext: Context): Flux[T] = wrapFlux[T](delegate.subscriberContext(mergeContext))
  def subscriberContext(doOnContext: Context => Context): Flux[T] = wrapFlux[T](delegate.subscriberContext(asJavaFn1(doOnContext)))

  def subscribeOn(scheduler: Scheduler): Flux[T] = wrapFlux[T](delegate.subscribeOn(scheduler))
  def subscribeOn(scheduler: Scheduler, requestOnSeparateThread: Boolean): Flux[T] = wrapFlux[T](delegate.subscribeOn(scheduler, requestOnSeparateThread))

  def subscribeWith[E <: Subscriber[T]](subscriber: E): E = delegate.subscribeWith(subscriber)

  def switchOnFirst[V](transformer: (Signal[T], Flux[T]) => Publisher[V]): Flux[V] = {
    val f: BiFunction[Signal[T], JFlux[T], Publisher[V]] = asJavaFn2[Signal[T], JFlux[T], Publisher[V]]((s: Signal[T], flux: JFlux[T]) => transformer(s, wrapFlux[T](flux)))
    wrapFlux[V](delegate.switchOnFirst[V](f.asInstanceOf[BiFunction[Signal[_ <: T], JFlux[T], Publisher[_ <: V]]]))
  }

  def switchIfEmpty(alternate: Publisher[T]): Flux[T] = wrapFlux[T](delegate.switchIfEmpty(alternate))

  def switchMap[V](fn: (_ >: T) => Publisher[_ <: V]): Flux[V] = wrapFlux[V](delegate.switchMap[V](asJavaFn1(fn)))
  def switchMap[V](fn: (_ >: T) => Publisher[_ <: V], prefetch: Int): Flux[V] = wrapFlux[V](delegate.switchMap[V](asJavaFn1(fn), prefetch))

  def tag(key: String, value: String): Flux[T] = wrapFlux[T](delegate.tag(key, value))

  def take(n: Long): Flux[T] = wrapFlux[T](delegate.take(n))
  def take(timespan: FiniteDuration): Flux[T] = wrapFlux[T](delegate.take(asJavaDuration(timespan)))
  def take(timespan: FiniteDuration, scheduler: Scheduler): Flux[T] = wrapFlux[T](delegate.take(asJavaDuration(timespan), scheduler))

  def takeLast(n: Int): Flux[T] = wrapFlux[T](delegate.takeLast(n))

  def takeUntil(predicate: T => Boolean): Flux[T] = wrapFlux[T](delegate.takeUntil(asJavaPredicate(predicate)))
  def takeUntilOther(other: Publisher[_]): Flux[T] = wrapFlux[T](delegate.takeUntilOther(other))

  def takeWhile(continuePredicate: T => Boolean): Flux[T] = wrapFlux[T](delegate.takeWhile(asJavaPredicate(continuePredicate)))

  def then(): Mono[Unit] = wrapMono[Void](delegate.`then`()).map(_ => ())
  def then[V](other: Mono[V]): Mono[V] = wrapMono[V](delegate.`then`(other.delegate.asInstanceOf[JMono[V]]))

  def thenEmpty(other: Publisher[Unit]): Mono[Unit] = wrapMono[Unit](delegate.thenEmpty(Flux.from(other).map[Void](_ => null: Void)).map(_ => Unit))

  def thenMany[V](other: Publisher[V]): Flux[V] = wrapFlux[V](delegate.thenMany(other))

  def timeout(timeout: FiniteDuration): Flux[T] = wrapFlux[T](delegate.timeout(asJavaDuration(timeout)))
  def timeout(timeout: FiniteDuration, fallback: Publisher[T]): Flux[T] = wrapFlux[T](delegate.timeout(asJavaDuration(timeout), fallback))
  def timeout(timeout: FiniteDuration, timer: Scheduler): Flux[T] = wrapFlux[T](delegate.timeout(asJavaDuration(timeout), timer))
  def timeout(timeout: FiniteDuration, fallback: Publisher[T], timer: Scheduler): Flux[T] = wrapFlux[T](delegate.timeout(asJavaDuration(timeout), fallback, timer))
  def timeout[U](firstTimeout: Publisher[U]): Flux[T] = wrapFlux[T](delegate.timeout[U](firstTimeout))
  def timeout[U, V](firstTimeout: Publisher[U], nextTimeoutFactory: T => Publisher[V]): Flux[T] = wrapFlux[T](delegate.timeout[U, V](firstTimeout, asJavaFn1(nextTimeoutFactory)))
  def timeout[U, V](firstTimeout: Publisher[U], nextTimeoutFactory: T => Publisher[V], fallback: Publisher[T]): Flux[T] = wrapFlux[T](delegate.timeout[U, V](firstTimeout, asJavaFn1(nextTimeoutFactory), fallback))

  def timestamp(): Flux[(Long, T)] = wrapFlux[(Long, T)](delegate.timestamp().map(t => toScalaTuple2(t.mapT1(t1 => Long2long(t1)))))
  def timestamp(scheduler: Scheduler): Flux[(Long, T)] = wrapFlux[(Long, T)](delegate.timestamp(scheduler).map(t => toScalaTuple2(t.mapT1(t1 => Long2long(t1)))))

  def toIterable(): Iterable[T] = toScalaIterable(delegate.toIterable())
  def toIterable(batchSize: Int): Iterable[T] = toScalaIterable(delegate.toIterable(batchSize))
  def toIterable(batchSize: Int, queueProvider: () => util.Queue[T]): Iterable[T] = toScalaIterable(delegate.toIterable(batchSize, asJavaSupplier(queueProvider)))

  // toSeq is not part of Java api. Added for convenience.
  def toSeq(): Iterable[T] = toScalaIterable(delegate.toIterable()).toSeq
  def toSeq(batchSize: Int): Iterable[T] = toScalaIterable(delegate.toIterable(batchSize)).toSeq

  def toStream(): Stream[T] = asScalaIterator(delegate.toStream().iterator()).toStream
  def toStream(batchSize: Int): Stream[T] = asScalaIterator(delegate.toStream(batchSize).iterator()).toStream

  def transform[V](transformer: Flux[T] => Publisher[V]): Flux[V] = wrapFlux(delegate.transform[V](asJavaFn1((jflux: JFlux[T]) => transformer(wrapFlux(jflux)))))
  def transformDeferred[V](transformer: Flux[T] => Publisher[V]): Flux[V] = wrapFlux(delegate.transformDeferred[V](asJavaFn1((jflux: JFlux[T]) => transformer(wrapFlux(jflux)))))

  def window(maxSize: Int): Flux[Flux[T]] = wrapFlux[Flux[T]](delegate.window(maxSize).map(wrapFlux[T](_)))
  def window(maxSize: Int, skip: Int): Flux[Flux[T]] = wrapFlux[Flux[T]](delegate.window(maxSize, skip).map(wrapFlux[T](_)))
  def window(boundary: Publisher[_]): Flux[Flux[T]] = wrapFlux[Flux[T]](delegate.window(boundary).map(wrapFlux[T](_)))
  def window(windowingTimespan: FiniteDuration): Flux[Flux[T]] = wrapFlux[Flux[T]](delegate.window(asJavaDuration(windowingTimespan)).map(wrapFlux[T](_)))
  def window(windowingTimespan: FiniteDuration, openWindowEvery: FiniteDuration): Flux[Flux[T]] = wrapFlux[Flux[T]](delegate.window(asJavaDuration(windowingTimespan), asJavaDuration(openWindowEvery)).map(wrapFlux[T](_)))
  def window(windowingTimespan: FiniteDuration, timer: Scheduler): Flux[Flux[T]] = wrapFlux[Flux[T]](delegate.window(asJavaDuration(windowingTimespan), timer).map(wrapFlux[T](_)))
  def window(windowingTimespan: FiniteDuration, openWindowEvery: FiniteDuration, timer: Scheduler): Flux[Flux[T]] = wrapFlux[Flux[T]](delegate.window(asJavaDuration(windowingTimespan), asJavaDuration(openWindowEvery), timer).map(wrapFlux[T](_)))

  def windowTimeout(maxSize: Int, maxTime: FiniteDuration): Flux[Flux[T]] = wrapFlux[Flux[T]](delegate.windowTimeout(maxSize, asJavaDuration(maxTime)).map(wrapFlux[T](_)))
  def windowTimeout(maxSize: Int, maxTime: FiniteDuration, timer: Scheduler): Flux[Flux[T]] = wrapFlux[Flux[T]](delegate.windowTimeout(maxSize, asJavaDuration(maxTime), timer).map(wrapFlux[T](_)))

  def windowUntil(boundaryTrigger: T => Boolean): Flux[Flux[T]] = wrapFlux[Flux[T]](delegate.windowUntil(asJavaPredicate(boundaryTrigger)).map(wrapFlux[T](_)))
  def windowUntil(boundaryTrigger: T => Boolean, cutBefore: Boolean): Flux[Flux[T]] = wrapFlux[Flux[T]](delegate.windowUntil(asJavaPredicate(boundaryTrigger), cutBefore).map(wrapFlux[T](_)))
  def windowUntil(boundaryTrigger: T => Boolean, cutBefore: Boolean, prefetch: Int): Flux[Flux[T]] = wrapFlux[Flux[T]](delegate.windowUntil(asJavaPredicate(boundaryTrigger), cutBefore, prefetch).map(wrapFlux[T](_)))

  def windowWhile(inclusionPredicate: T => Boolean): Flux[Flux[T]] = wrapFlux[Flux[T]](delegate.windowWhile(asJavaPredicate(inclusionPredicate)).map(wrapFlux[T](_)))
  def windowWhile(inclusionPredicate: T => Boolean, prefetch: Int): Flux[Flux[T]] = wrapFlux[Flux[T]](delegate.windowWhile(asJavaPredicate(inclusionPredicate), prefetch).map(wrapFlux[T](_)))

  def windowWhen[U, V](bucketOpening: Publisher[U], closeSelector: U => Publisher[V]): Flux[Flux[T]] = wrapFlux[Flux[T]](delegate.windowWhen(bucketOpening, asJavaFn1(closeSelector)).map(wrapFlux[T](_)))

  def withLatestFrom[U, R](other: Publisher[U], resultSelector: (T, U) => R): Flux[R] = wrapFlux[R](delegate.withLatestFrom(other, asJavaFn2(resultSelector)))

  def zipWith[T2](source2: Publisher[T2]): Flux[(T, T2)] = wrapFlux[(T, T2)](delegate.zipWith(source2).map(toScalaTuple2(_)))
  def zipWith[T2](source2: Publisher[T2], prefetch: Int): Flux[(T, T2)] = wrapFlux[(T, T2)](delegate.zipWith(source2, prefetch).map(toScalaTuple2(_)))
  def zipWith[T2, V](source2: Publisher[T2], combinator: (T, T2) => V): Flux[V] = wrapFlux[V](delegate.zipWith(source2, asJavaFn2(combinator)))
  def zipWith[T2, V](source2: Publisher[T2], prefetch: Int, combinator: (T, T2) => V): Flux[V] = wrapFlux[V](delegate.zipWith(source2, prefetch, asJavaFn2(combinator)))

  def zipWithIterable[T2](iterable: Iterable[T2]): Flux[(T, T2)] = wrapFlux[(T, T2)](delegate.zipWithIterable(asJavaIterable(iterable)).map(toScalaTuple2(_)))
  def zipWithIterable[T2, V](iterable: Iterable[T2], zipper: (T, T2) => V): Flux[V] = wrapFlux[V](delegate.zipWithIterable(asJavaIterable(iterable), asJavaFn2(zipper)))

}

private[stream] class FluxImpl[T](publisher: Publisher[T]) extends Flux[T] with Scannable {
  override private[stream] val delegate = JFlux.from(publisher)
  override private[stream] val jscannable = JScannable.from(delegate)
}