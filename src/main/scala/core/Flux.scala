package core

import java.{lang, util}
import java.util.function.{Consumer, Supplier, Function => JFunction}
import java.time.{Duration => JDuration}
import java.util.concurrent.Callable
import java.util.stream.Collector

import org.reactivestreams.{Publisher, Subscriber, Subscription}
import reactor.core.{Disposable, publisher}
import reactor.core.publisher.FluxSink.OverflowStrategy
import publisher.{FluxSink, Signal, SignalType, SynchronousSink, Flux => JFlux, Mono => JMono}
import reactor.test.StepVerifier
import reactor.test.scheduler.VirtualTimeScheduler
import reactor.util.concurrent.Queues.{SMALL_BUFFER_SIZE, XS_BUFFER_SIZE}
import core.JavaInterop._
import reactor.util.function.{Tuple2 => JTuple2}
import reactor.util.function.{Tuple3 => JTuple3}
import reactor.util.function.{Tuple4 => JTuple4}
import reactor.util.function.{Tuple5 => JTuple5}
import reactor.util.function.{Tuple6 => JTuple6}
import reactor.util.function.{Tuple7 => JTuple7}
import reactor.util.function.{Tuple8 => JTuple8}
import reactor.core.scheduler.Scheduler
import reactor.util.context.Context
import reactor.util.function.Tuples

import scala.collection.convert.Wrappers.IterableWrapper
import scala.collection.{JavaConverters, mutable}
import scala.concurrent.duration.{Duration, FiniteDuration}
import scala.concurrent.duration.Duration.Infinite
import scala.language.{existentials, higherKinds}


// TODO make sure any and all @Nullable methods are converted to Option
object Flux extends ImplicitJavaInterop {

  ///
  /// API METHODS
  ///

  def combineLatest[T, V](combinator: Array[AnyRef] => V, sources: Publisher[T]*): Flux[V] = wrapFlux[V](JFlux.combineLatest(combinator, sources:_*))
  def combineLatest[T, V](combinator: Array[AnyRef] => V, prefetch: Int, sources: Publisher[T]*): Flux[V] = wrapFlux[V](JFlux.combineLatest(combinator, prefetch, sources:_*))
  def combineLatest[T1, T2, V](source1: Publisher[T1], source2: Publisher[T2], combinator: (_ >: T1, _ >: T2) => _ <: V): Flux[V] = wrapFlux[V](JFlux.combineLatest(source1, source2, combinator))
  def combineLatest[T1 >: Any, T2 >: Any, T3 >: Any, V <: Any](source1: Publisher[T1], source2: Publisher[T2], source3: Publisher[T3], combinator: (_ >: T1, _ >: T2, _ >: T3) => V): Flux[V] = wrapFlux[V](JFlux.combineLatest(source1, source2, source3, combinator))
  def combineLatest[T1 >: Any, T2 >: Any, T3 >: Any, T4 >: Any, V <: Any](source1: Publisher[T1], source2: Publisher[T2], source3: Publisher[T3], source4: Publisher[T4], combinator: (_ >: T1, _ >: T2, _ >: T3, _ >: T4) => V): Flux[V] = wrapFlux[V](JFlux.combineLatest(source1, source2, source3, source4, combinator))
  def combineLatest[T1 >: Any, T2 >: Any, T3 >: Any, T4 >: Any, T5 >: Any, V <: Any](source1: Publisher[T1], source2: Publisher[T2], source3: Publisher[T3], source4: Publisher[T4], source5: Publisher[T5], combinator: (_ >: T1, _ >: T2, _ >: T3, _ >: T4, _ >: T5) => V): Flux[V] = wrapFlux[V](JFlux.combineLatest(source1, source2, source3, source4, source5, combinator))
  def combineLatest[T1 >: Any, T2 >: Any, T3 >: Any, T4 >: Any, T5 >: Any, T6 >: Any, V <: Any](source1: Publisher[T1], source2: Publisher[T2], source3: Publisher[T3], source4: Publisher[T4], source5: Publisher[T5], source6: Publisher[T6], combinator: (_ >: T1, _ >: T2, _ >: T3, _ >: T4, _ >: T5, _ >: T6) => V): Flux[V] = wrapFlux[V](JFlux.combineLatest(source1, source2, source3, source4, source5, source6, combinator))
  def combineLatest[T, V](sources: Iterable[_ <: Publisher[T]], combinator: Array[AnyRef] => V): Flux[V] = wrapFlux[V](JFlux.combineLatest(sources, combinator))
  def combineLatest[T, V](sources: Iterable[_ <: Publisher[T]], prefetch: Int, combinator: Array[AnyRef] => V): Flux[V] = wrapFlux[V](JFlux.combineLatest(sources, prefetch, combinator))

  def concat[T](sources: Iterable[_ <: Publisher[T]]): Flux[T] = wrapFlux[T](JFlux.concat(sources))
  def concatWithValues[T](sources: Publisher[T]*): Flux[T] = wrapFlux[T](JFlux.concat(sources:_*))
  def concat[T](sources: Publisher[_ <: Publisher[T]]): Flux[T] = wrapFlux[T](JFlux.concat(sources))
  def concat[T](sources: Publisher[_ <: Publisher[T]], prefetch: Int): Flux[T] = wrapFlux[T](JFlux.concat(sources, prefetch))
  def concat[T](sources: Publisher[T]*): Flux[T] = wrapFlux[T](JFlux.concat(sources:_*))

  def concatDelayError[T](sources: Publisher[_ <: Publisher[T]]): Flux[T] = wrapFlux[T](JFlux.concatDelayError(sources))
  def concatDelayError[T](sources: Publisher[_ <: Publisher[T]], prefetch: Int): Flux[T] = wrapFlux[T](JFlux.concatDelayError(sources, prefetch))
  def concatDelayError[T](sources: Publisher[_ <: Publisher[T]], delayUntilEnd: Boolean, prefetch: Int): Flux[T] = wrapFlux[T](JFlux.concatDelayError(sources, delayUntilEnd, prefetch))
  def concatDelayError[T](sources: Publisher[T]*): Flux[T] = wrapFlux[T](JFlux.concatDelayError(sources:_*))

  def create[T](emitter: FluxSink[T] => Unit): Flux[T] = wrapFlux[T](JFlux.create(emitter))
  def create[T](emitter: FluxSink[T] => Unit, backpressure: OverflowStrategy): Flux[T] = wrapFlux[T](JFlux.create(emitter, backpressure))

  def push[T](emitter: FluxSink[T] => Unit): Flux[T] = wrapFlux[T](JFlux.create(emitter))
  def push[T](emitter: FluxSink[T] => Unit, backpressure: OverflowStrategy): Flux[T] = wrapFlux[T](JFlux.create(emitter, backpressure))

  def defer[T](supplier: () => Publisher[T]): Flux[T] = wrapFlux[T](JFlux.defer(supplier))
  def deferWithContext[T](supplier: Context => Publisher[T]): Flux[T] = wrapFlux[T](JFlux.deferWithContext(supplier))

  def empty[T](): Flux[T] = wrapFlux[T](JFlux.empty())

  def error[T](error: Throwable): Flux[T] = wrapFlux[T](JFlux.error(error))
  def error[T](errorSupplier: () => Throwable): Flux[T] = wrapFlux[T](JFlux.error(errorSupplier))
  def error[T](error: Throwable, whenRequested: Boolean): Flux[T] = wrapFlux[T](JFlux.error(error))

  def first[T](source: Publisher[T], sources: Publisher[T]*): Flux[T] = first(source +: sources)
  def first[T](sources: Iterable[_ <: Publisher[T]]): Flux[T] = wrapFlux[T](JFlux.first(sources))

  def from[T](source: Publisher[T]): Flux[T] = wrapFlux[T](JFlux.from(source))
  def fromArray[T >: AnyRef](source: Array[T with AnyRef]): Flux[T] = wrapFlux[T](JFlux.fromArray(source))
  def fromIterable[T](source: Iterable[T]): Flux[T] = wrapFlux[T](JFlux.fromIterable(source))

  // scala stream can't directly map to java stream, so ignore this (since fromIterable will work with this)
//  def fromStream[T](stream: Stream[T]): Flux[T] = wrap(JFlux.fromStream(stream))
  def fromStream[T](stream: Stream[T]): Flux[T] = wrapFlux[T](JFlux.fromIterable(stream))
//  def fromStream[T](streamSupplier: () => Stream[T]): Flux[T] = wrap(JFlux.fromStream(streamSupplier))

  def generate[T](generator: SynchronousSink[T] => Unit): Flux[T] = wrapFlux[T](JFlux.generate(generator))
  def generate[T, S](stateSupplier: Callable[S], generator: (S, SynchronousSink[T]) => S): Flux[T] = wrapFlux[T](JFlux.generate(stateSupplier, generator))
  def generate[T, S](stateSupplier: Callable[S], generator: (S, SynchronousSink[T]) => S, stateConsumer: S => Unit): Flux[T] = wrapFlux[T](JFlux.generate(stateSupplier, generator, stateConsumer))


  def interval[T, S](period: Duration): Flux[Long] = period match {
    case _: Infinite => wrapFlux(JFlux.never())
    case finiteDuration: FiniteDuration => wrapFlux(JFlux.interval(finiteDuration).map(Long2long))
  }

  def interval[T, S](delay: Duration, period: Duration): Flux[Long] = (delay, period) match {
    case (_: Infinite, _: Infinite) => wrapFlux(JFlux.never())
    case (_: Infinite, period: FiniteDuration) => wrapFlux(JFlux.never())
    case (delay: FiniteDuration, _: Infinite) => ??? // todo implement once more timing methods added
    case (delay: FiniteDuration, period: FiniteDuration) => wrapFlux(JFlux.interval(delay, period).map(Long2long))
  }

  def interval[T, S](period: Duration, timer: Scheduler): Flux[Long] = period match {
    case _: Infinite => wrapFlux(JFlux.never())
    case finiteDuration: FiniteDuration => wrapFlux(JFlux.interval(finiteDuration, timer).map(Long2long))
  }

  def interval[T, S](delay: Duration, period: Duration, timer: Scheduler): Flux[Long] = (delay, period) match {
    case (_: Infinite, _: Infinite) => wrapFlux(JFlux.never())
    case (_: Infinite, period: FiniteDuration) => wrapFlux(JFlux.never())
    case (delay: FiniteDuration, _: Infinite) => ??? // todo implement once more timing methods added
    case (delay: FiniteDuration, period: FiniteDuration) => wrapFlux(JFlux.interval(delay, period, timer).map(Long2long))
  }

  def just[T](data: T*): Flux[T] = wrapFlux[T](JFlux.just(data:_*))

  def merge[T](source: Publisher[Publisher[T]]): Flux[T] = wrapFlux[T](JFlux.merge(source))
  def merge[T](source: Publisher[Publisher[T]], concurrency: Int): Flux[T] = wrapFlux[T](JFlux.merge(source, concurrency))
  def merge[T](source: Publisher[Publisher[T]], concurrency: Int, prefetch: Int): Flux[T] = wrapFlux[T](JFlux.merge(source, concurrency, prefetch))
  def merge[T](sources: Iterable[Publisher[T]], concurrency: Int, prefetch: Int): Flux[T] = wrapFlux[T](JFlux.merge(sources))
  def merge[T](sources: Publisher[T]*): Flux[T] = wrapFlux[T](JFlux.merge(sources:_*))
  def merge[T](prefetch: Int, sources: Publisher[T]*): Flux[T] = wrapFlux[T](JFlux.merge(sources:_*))

  def mergeDelayError[T](prefetch: Int, sources: Publisher[T]): Flux[T] = wrapFlux[T](JFlux.mergeDelayError(prefetch, sources))

  // todo reason to have T in publisher? compiles either way and reduces freedom?
  def mergeOrdered[T <: Ordered[T]](sources: Publisher[T]): Flux[T] = wrapFlux[T](JFlux.mergeOrdered(sources))
  def mergeOrdered[T](ordering: Ordering[T], sources: Publisher[T]): Flux[T] = wrapFlux[T](JFlux.mergeOrdered(ordering, sources))
  def mergeOrdered[T](prefetch: Int, ordering: Ordering[T], sources: Publisher[T]): Flux[T] = wrapFlux[T](JFlux.mergeOrdered(prefetch, ordering, sources))

  def mergeSequential[T](sources: Publisher[_ <: Publisher[T]]): Flux[T] = wrapFlux[T](JFlux.mergeSequential(sources))
  def mergeSequential[T](sources: Publisher[_ <: Publisher[T]], maxConcurrency: Int, prefetch: Int): Flux[T] = wrapFlux[T](JFlux.mergeSequential(sources, maxConcurrency, prefetch))
  def mergeSequentialDelayError[T](sources: Publisher[_ <: Publisher[T]], maxConcurrency: Int, prefetch: Int): Flux[T] = wrapFlux[T](JFlux.mergeSequentialDelayError(sources, maxConcurrency, prefetch))
  def mergeSequential[T](sources: Publisher[T]*): Flux[T] = wrapFlux[T](JFlux.mergeSequential(sources:_*))
  def mergeSequential[T](prefetch: Int, sources: Publisher[T]*): Flux[T] = wrapFlux[T](JFlux.mergeSequential(prefetch, sources:_*))
  def mergeSequentialDelayError[T](prefetch: Int, sources: Publisher[T]*): Flux[T] = wrapFlux[T](JFlux.mergeSequentialDelayError(prefetch, sources:_*))
  def mergeSequential[T](sources: Iterable[_ <: Publisher[T]]): Flux[T] = wrapFlux[T](JFlux.mergeSequential(sources))
  def mergeSequential[T](sources: Iterable[_ <: Publisher[T]], maxConcurrency: Int, prefetch: Int): Flux[T] = wrapFlux[T](JFlux.mergeSequential(sources, maxConcurrency, prefetch))
  def mergeSequentialDelayError[T](sources: Iterable[_ <: Publisher[T]], maxConcurrency: Int, prefetch: Int): Flux[T] = wrapFlux[T](JFlux.mergeSequentialDelayError(sources, maxConcurrency, prefetch))

  def never[T](): Flux[T] = wrapFlux[T](JFlux.never())

  def range(start: Int, count: Int): Flux[Int] = wrapFlux[Int](JFlux.range(start, count).map(_.toInt))

  def switchOnNext[T](mergedPublishers: Publisher[_ <: Publisher[T]]): Flux[T] = wrapFlux[T](JFlux.switchOnNext(mergedPublishers))
  def switchOnNext[T](mergedPublishers: Publisher[_ <: Publisher[T]], prefetch: Int): Flux[T] = wrapFlux[T](JFlux.switchOnNext(mergedPublishers, prefetch))

  def using[T, D](resourceSupplier: Callable[D], sourceSupplier: D => Publisher[T], resourceCleanup: D => Unit): Flux[T] = wrapFlux[T](JFlux.using(resourceSupplier, sourceSupplier, resourceCleanup))
  def using[T, D](resourceSupplier: Callable[D], sourceSupplier: D => Publisher[T], resourceCleanup: D => Unit, eager: Boolean): Flux[T] = wrapFlux[T](JFlux.using(resourceSupplier, sourceSupplier, resourceCleanup, eager))

  def usingWhen[T, D](resourceSupplier: Publisher[D], resourceClosure: D => Publisher[T], asyncComplete: D => Publisher[_], asyncError: D => Publisher[_]): Flux[T] = wrapFlux[T](JFlux.usingWhen(resourceSupplier, resourceClosure, asyncComplete, asyncError))
  def usingWhen[T, D](resourceSupplier: Publisher[D], resourceClosure: D => Publisher[T], asyncComplete: D => Publisher[_], asyncError: D => Publisher[_], asyncCancel: D => Publisher[_]): Flux[T] = wrapFlux[T](JFlux.usingWhen(resourceSupplier, resourceClosure, asyncComplete, asyncError, asyncCancel))
  def usingWhen[T, D](resourceSupplier: Publisher[D], resourceClosure: D => Publisher[T], asyncCleanup: D => Publisher[_]): Flux[T] = wrapFlux[T](JFlux.usingWhen(resourceSupplier, resourceClosure, asyncCleanup))

  def zip[T1, T2, O](source1: Publisher[T1], source2: Publisher[T2], combinator: (T1, T2) => O): Flux[O] = wrapFlux[O](JFlux.zip(source1, source2, combinator))

  def zip[T1, T2](source1: Publisher[T1], source2: Publisher[T2]): Flux[(T1, T2)] = wrapFlux(JFlux.zip(source1, source2).map(toScalaTuple2(_)))
  def zip[T1, T2, T3](source1: Publisher[T1], source2: Publisher[T2], source3: Publisher[T3]): Flux[(T1, T2, T3)] = wrapFlux(JFlux.zip(source1, source2, source3).map(toScalaTuple3(_)))
  def zip[T1, T2, T3, T4](source1: Publisher[T1], source2: Publisher[T2], source3: Publisher[T3], source4: Publisher[T4]): Flux[(T1, T2, T3, T4)] = wrapFlux(JFlux.zip(source1, source2, source3, source4).map(toScalaTuple4(_)))
  def zip[T1, T2, T3, T4, T5](source1: Publisher[T1], source2: Publisher[T2], source3: Publisher[T3], source4: Publisher[T4], source5: Publisher[T5]): Flux[(T1, T2, T3, T4, T5)] = wrapFlux(JFlux.zip(source1, source2, source3, source4, source5).map(toScalaTuple5(_)))
  def zip[T1, T2, T3, T4, T5, T6](source1: Publisher[T1], source2: Publisher[T2], source3: Publisher[T3], source4: Publisher[T4], source5: Publisher[T5], source6: Publisher[T6]): Flux[(T1, T2, T3, T4, T5, T6)] = wrapFlux(JFlux.zip(source1, source2, source3, source4, source5, source6).map(toScalaTuple6(_)))
  def zip[T1, T2, T3, T4, T5, T6, T7](source1: Publisher[T1], source2: Publisher[T2], source3: Publisher[T3], source4: Publisher[T4], source5: Publisher[T5], source6: Publisher[T6], source7: Publisher[T7]): Flux[(T1, T2, T3, T4, T5, T6, T7)] = wrapFlux(JFlux.zip(source1, source2, source3, source4, source5, source6, source7).map(toScalaTuple7(_)))
  def zip[T1, T2, T3, T4, T5, T6, T7, T8](source1: Publisher[T1], source2: Publisher[T2], source3: Publisher[T3], source4: Publisher[T4], source5: Publisher[T5], source6: Publisher[T6], source7: Publisher[T7], source8: Publisher[T8]): Flux[(T1, T2, T3, T4, T5, T6, T7, T8)] = wrapFlux(JFlux.zip(source1, source2, source3, source4, source5, source6, source7, source8).map(toScalaTuple8(_)))

  def zip[O](sources: Iterable[Publisher[_]], combinator: (_ >: Array[AnyRef]) => O): Flux[O] = wrapFlux[O](JFlux.zip(sources, combinator))
  def zip[O](sources: Iterable[Publisher[_]], prefetch: Int, combinator: (_ >: Array[AnyRef]) => O): Flux[O] = wrapFlux[O](JFlux.zip(sources, prefetch, combinator))

  def zip[I, O](combinator: (_ >: Array[AnyRef]) => O, sources: Publisher[I]*): Flux[O] = wrapFlux[O](JFlux.zip(combinator, sources:_*))
  def zip[I, O](combinator: (_ >: Array[AnyRef]) => O, prefetch: Int, sources: Publisher[I]*): Flux[O] = wrapFlux[O](JFlux.zip(combinator, prefetch, sources:_*))

  def zip[V](sources: Publisher[_ <: Publisher[_]], combinator: (Any, Any) => V): Flux[V] = wrapFlux[V](JFlux.zip(sources, (tuple: JTuple2[Any, Any]) => combinator(tuple.getT1, tuple.getT2)))
  def zip[V](sources: Publisher[_ <: Publisher[_]], combinator: (Any, Any, Any) => V): Flux[V] = wrapFlux[V](JFlux.zip(sources, (tuple: JTuple3[Any, Any, Any]) => combinator(tuple.getT1, tuple.getT2, tuple.getT3)))
  def zip[V](sources: Publisher[_ <: Publisher[_]], combinator: (Any, Any, Any, Any) => V): Flux[V] = wrapFlux[V](JFlux.zip(sources, (tuple: JTuple4[Any, Any, Any, Any]) => combinator(tuple.getT1, tuple.getT2, tuple.getT3, tuple.getT4)))
  def zip[V](sources: Publisher[_ <: Publisher[_]], combinator: (Any, Any, Any, Any, Any) => V): Flux[V] = wrapFlux[V](JFlux.zip(sources, (tuple: JTuple5[Any, Any, Any, Any, Any]) => combinator(tuple.getT1, tuple.getT2, tuple.getT3, tuple.getT4, tuple.getT5)))
  def zip[V](sources: Publisher[_ <: Publisher[_]], combinator: (Any, Any, Any, Any, Any, Any) => V): Flux[V] = wrapFlux[V](JFlux.zip(sources, (tuple: JTuple6[Any, Any, Any, Any, Any, Any]) => combinator(tuple.getT1, tuple.getT2, tuple.getT3, tuple.getT4, tuple.getT5, tuple.getT6)))
  def zip[V](sources: Publisher[_ <: Publisher[_]], combinator: (Any, Any, Any, Any, Any, Any, Any) => V): Flux[V] = wrapFlux[V](JFlux.zip(sources, (tuple: JTuple7[Any, Any, Any, Any, Any, Any, Any]) => combinator(tuple.getT1, tuple.getT2, tuple.getT3, tuple.getT4, tuple.getT5, tuple.getT6, tuple.getT7)))
  def zip[V](sources: Publisher[_ <: Publisher[_]], combinator: (Any, Any, Any, Any, Any, Any, Any, Any) => V): Flux[V] = wrapFlux[V](JFlux.zip(sources, (tuple: JTuple8[Any, Any, Any, Any, Any, Any, Any, Any]) => combinator(tuple.getT1, tuple.getT2, tuple.getT3, tuple.getT4, tuple.getT5, tuple.getT6, tuple.getT7, tuple.getT8)))

}

final class Flux[T] (private val publisher: Publisher[T]) extends Publisher[T] with ImplicitJavaInterop {

  private val delegate: JFlux[T] = JFlux.from(publisher)

  ///
  /// API METHODS
  ///

  def all(predicate: T => Boolean): Mono[Boolean] = wrapMono(delegate.all(predicate)).asInstanceOf[Mono[Boolean]]
  def any(predicate: T => Boolean): Mono[Boolean] = wrapMono(delegate.any(predicate)).asInstanceOf[Mono[Boolean]]

  def as[P](transformer: Flux[T] => P): P = delegate.as((jf: JFlux[T]) => transformer.apply(wrapFlux(jf)))

  def blockFirst(): Option[T] = Option(delegate.blockFirst())

  def blockFirst(timeout: Duration): Option[T] = timeout match {
    case _: Infinite => Option(delegate.blockFirst())
    case finiteDuration: FiniteDuration => Option(delegate.blockLast(finiteDuration))
  }

  def blockLast(): Option[T] = Option(delegate.blockLast())

  def blockLast(timeout: Duration): Option[T] = timeout match {
    case _: Infinite => Option(delegate.blockLast())
    case finiteDuration: FiniteDuration => Option(delegate.blockLast(finiteDuration))
  }

  // todo benchmark toSeq!

  def buffer(): Flux[Seq[T]] = wrapFlux(delegate.buffer()).map(toSeq)
  def buffer(maxSize: Int): Flux[Seq[T]] = wrapFlux(delegate.buffer(maxSize)).map(toSeq)
  def buffer(maxSize: Int, skip: Int): Flux[Seq[T]] = wrapFlux(delegate.buffer(maxSize, skip)).map(toSeq)
  def buffer(other: Publisher[T]): Flux[Seq[T]] = wrapFlux(delegate.buffer(other)).map(toSeq)

  def buffer(bufferingTimespan: FiniteDuration): Flux[Seq[T]] = wrapFlux(delegate.buffer(bufferingTimespan)).map(toSeq)
  def buffer(bufferingTimespan: FiniteDuration, openBufferEvery: FiniteDuration): Flux[Seq[T]] = wrapFlux(delegate.buffer(bufferingTimespan, openBufferEvery)).map(toSeq)
  def buffer(bufferingTimespan: FiniteDuration, timer: Scheduler): Flux[Seq[T]] = wrapFlux(delegate.buffer(bufferingTimespan, timer)).map(toSeq)
  def buffer(bufferingTimespan: FiniteDuration, openBufferEvery: FiniteDuration, timer: Scheduler): Flux[Seq[T]] = wrapFlux(delegate.buffer(bufferingTimespan, openBufferEvery, timer)).map(toSeq)

  def bufferTimeout(maxSize: Int, maxTime: FiniteDuration): Flux[Seq[T]] = wrapFlux(delegate.bufferTimeout(maxSize, maxTime)).map(toSeq)
  // todo missing custom collection wrapper here (see JavaInterop's toIterable(collection: Collection) method)
  def bufferTimeout(maxSize: Int, maxTime: FiniteDuration, timer: Scheduler): Flux[Seq[T]] = wrapFlux(delegate.bufferTimeout(maxSize, maxTime, timer)).map(toSeq)
  // todo missing custom collection wrapper here

  def bufferUntil(predicate: T => Boolean): Flux[Seq[T]] = wrapFlux(delegate.bufferUntil(predicate)).map(toSeq)
  def bufferUntil(predicate: T => Boolean, cutBefore: Boolean): Flux[Seq[T]] = wrapFlux(delegate.bufferUntil(predicate, cutBefore)).map(toSeq)

  def bufferWhile(predicate: T => Boolean): Flux[Seq[T]] = wrapFlux(delegate.bufferWhile(predicate)).map(toSeq)

  def bufferWhen[U, V](bucketOpening: Publisher[U], closeSelector: U => Publisher[V]): Flux[Seq[T]] = wrapFlux(delegate.bufferWhen(bucketOpening, closeSelector)).map(toSeq)
  // todo missing custom collection wrapper here (see JavaConverters.asJavaCollection())

  def cache(): Flux[T] = wrapFlux[T](delegate.cache())
  def cache(history: Int): Flux[T] = wrapFlux[T](delegate.cache(history))
  def cache(ttl: Duration): Flux[T] = ttl match {
    case _: Infinite => ???
    case finiteDuration: FiniteDuration => wrapFlux(delegate.cache(finiteDuration))
  }
  def cache(ttl: Duration, timer: Scheduler): Flux[T] = ttl match {
    case _: Infinite => ???
    case finiteDuration: FiniteDuration => wrapFlux(delegate.cache(finiteDuration, timer))
  }
  def cache(history: Int, ttl: Duration): Flux[T] = ttl match {
    case _: Infinite => ???
    case finiteDuration: FiniteDuration => wrapFlux(delegate.cache(history, finiteDuration))
  }
  def cache(history: Int, ttl: Duration, timer: Scheduler): Flux[T] = ttl match {
    case _: Infinite => ???
    case finiteDuration: FiniteDuration => wrapFlux(delegate.cache(history, finiteDuration, timer))
  }

  def cast[E](clazz: Class[E]): Flux[E] = wrapFlux[E](delegate.cast(clazz))

  def cancelOn(scheduler: Scheduler): Flux[T] = wrapFlux[T](delegate.cancelOn(scheduler))

  def checkpoint(): Flux[T] = wrapFlux[T](delegate.checkpoint())
  def checkpoint(description: String): Flux[T] = wrapFlux[T](delegate.checkpoint(description))
  def checkpoint(description: Option[String], forceStackTrace: Boolean): Flux[T] = description match {
    case Some(desc) => wrapFlux(delegate.checkpoint(desc, forceStackTrace))
    case None => wrapFlux(delegate.checkpoint(null, forceStackTrace)) // this java api accepts Nullable
  }

  def collect[E](containerSupplier: () => E, collector: (E, T) => Unit): Mono[E] = wrapMono(delegate.collect(containerSupplier, collector))
  def collect[R, A](collector: Collector[T, A, R]): Mono[R] = wrapMono(delegate.collect(collector))
  def collectSeq(): Mono[Seq[T]] = wrapMono(delegate.collectList()).map(toSeq)

  def collectMap[K](keyExtractor: T => K): Mono[_ <: Map[K, T]] = {
    // this explicit type def is required
    val mono: Mono[util.Map[K, T]] = wrapMono(delegate.collectMap(keyExtractor))
    mono.map(toMap)
  }

  def collectMap[K, V](keyExtractor: T => K, valueExtractor: T => V): Mono[_ <: Map[K, V]] = {
    // this explicit type def is required
    val mono: Mono[util.Map[K, V]] = wrapMono(delegate.collectMap(keyExtractor, valueExtractor))
    mono.map(toMap)
  }

  def collectMap[K, V](keyExtractor: T => K, valueExtractor: T => V, mapSupplier: () => mutable.Map[K, V]): Mono[_ <: Map[K, V]] = {
    // this explicit type def is required
    val mono: Mono[util.Map[K, V]] = wrapMono(delegate.collectMap(keyExtractor, valueExtractor, () => asJavaMutableMap(mapSupplier())))
    mono.map(toMap)
  }

  def collectMultimap[K](keyExtractor: T => K): Mono[_ <: Map[K, Iterable[T]]] = {
    // this explicit type def is required
    val mono: Mono[util.Map[K, util.Collection[T]]] = wrapMono(delegate.collectMultimap(keyExtractor))
    mono.map(toMap).map(map => map.mapValues(toIterable))
  }

  def collectMultimap[K, V](keyExtractor: T => K, valueExtractor: T => V): Mono[_ <: Map[K, Iterable[V]]] = {
    // this explicit type def is required
    val mono: Mono[util.Map[K, util.Collection[V]]] = wrapMono(delegate.collectMultimap(keyExtractor, valueExtractor))
    mono.map(toMap).map(map => map.mapValues(toIterable))
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

  def collectSortedList(): Mono[Seq[T]] = wrapMono(delegate.collectSortedList().map(toSeq(_)))
  def collectSortedList(ordering: Ordering[T]): Mono[Seq[T]] = wrapMono(delegate.collectSortedList(ordering).map(toSeq(_)))

  // will be removed 3.4
//  def compose[V](transformer: Flux[T] => Publisher[V]): Flux[V] = wrapFlux[V](delegate.compose(transformer))

  def concatMap[V](mapper: T => Publisher[V]): Flux[V] = wrapFlux[V](delegate.concatMap(mapper))
  def concatMap[V](mapper: T => Publisher[V], prefetch: Int): Flux[V] = wrapFlux[V](delegate.concatMap(mapper, prefetch))

  def concatMapDelayError[V](mapper: T => Publisher[V]): Flux[V] = wrapFlux[V](delegate.concatMapDelayError(mapper))
  def concatMapDelayError[V](mapper: T => Publisher[V], prefetch: Int): Flux[V] = wrapFlux[V](delegate.concatMapDelayError(mapper, prefetch))
  def concatMapDelayError[V](mapper: T => Publisher[V], delayUntilEnd: Boolean, prefetch: Int): Flux[V] = wrapFlux[V](delegate.concatMapDelayError(mapper, delayUntilEnd, prefetch))

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

  def delayElements(delay: FiniteDuration): Flux[T] = wrapFlux[T](delegate.delayElements(delay))
  def delayElements(delay: FiniteDuration, timer: Scheduler): Flux[T] = wrapFlux[T](delegate.delayElements(delay, timer))

  def delaySequence(delay: FiniteDuration): Flux[T] = wrapFlux[T](delegate.delaySequence(delay))
  def delaySequence(delay: FiniteDuration, timer: Scheduler): Flux[T] = wrapFlux[T](delegate.delaySequence(delay, timer))

  def delayUntil(triggerProvider: T => Publisher[_]): Flux[T] = wrapFlux[T](delegate.delayUntil(triggerProvider))

  def delaySubscription(delay: FiniteDuration): Flux[T] = wrapFlux[T](delegate.delaySubscription(delay))
  def delaySubscription(delay: FiniteDuration, timer: Scheduler): Flux[T] = wrapFlux[T](delegate.delaySubscription(delay, timer))
  def delaySubscription[U](subscriptionDelay: Publisher[U]): Flux[T] = wrapFlux[T](delegate.delaySubscription(subscriptionDelay))

  def dematerialize[X](): Flux[X] = wrapFlux[X](delegate.dematerialize())

  def distinct(): Flux[T] = wrapFlux[T](delegate.distinct())
  def distinct[V](keySelector: T => V): Flux[T] = wrapFlux[T](delegate.distinct[V](keySelector))

  // todo: missing two mutable supplied collection using methods. Should try to include?

  def distinctUntilChanged(): Flux[T] = wrapFlux[T](delegate.distinctUntilChanged())
  def distinctUntilChanged[V](keySelector: T => V): Flux[T] = wrapFlux[T](delegate.distinctUntilChanged[V](keySelector))
  def distinctUntilChanged[V](keySelector: T => V, keyComparator: (V, V) => Boolean): Flux[T] = wrapFlux[T](delegate.distinctUntilChanged[V](keySelector, keyComparator))

  def doAfterTerminate(afterTerminate: () => Unit): Flux[T] = wrapFlux[T](delegate.doAfterTerminate(afterTerminate))

  def doOnCancel(onCancel: () => Unit): Flux[T] = wrapFlux[T](delegate.doOnCancel(onCancel))
  def doOnComplete(onComplete: () => Unit): Flux[T] = wrapFlux[T](delegate.doOnComplete(onComplete))
  def doOnDiscard[R](classType: Class[R], discardHook: R => Unit): Flux[T] = wrapFlux[T](delegate.doOnDiscard(classType, discardHook))
  def doOnEach(signalConsumer: Signal[T] => Unit): Flux[T] = wrapFlux[T](delegate.doOnEach(signalConsumer))
  def doOnError(onError: Throwable => Unit): Flux[T] = wrapFlux[T](delegate.doOnError(onError))
  def doOnError[E <: Throwable](exceptionType: Class[E], onError: Throwable => Unit): Flux[T] = wrapFlux[T](delegate.doOnError[E](exceptionType, onError))
  def doOnError(predicate: Throwable => Boolean, onError: Throwable => Unit): Flux[T] = wrapFlux[T](delegate.doOnError(predicate, onError))
  def doOnNext(onNext: T => Unit): Flux[T] = wrapFlux[T](delegate.doOnNext(onNext))
  def doOnRequest(consumer: Long => Unit): Flux[T] = wrapFlux[T](delegate.doOnRequest(consumer))
  def doOnSubscribe(onSubscribe: Subscription => Unit): Flux[T] = wrapFlux[T](delegate.doOnSubscribe(onSubscribe))
  def doOnTerminate(onTerminate: () => Unit): Flux[T] = wrapFlux[T](delegate.doOnTerminate(onTerminate))
  def doFirst(onFirst: () => Unit): Flux[T] = wrapFlux[T](delegate.doFirst(onFirst))
  def doFinally(onFinally: SignalType => Unit): Flux[T] = wrapFlux[T](delegate.doFinally(onFinally))

  def elapsed(): Flux[(Long, T)] = wrapFlux[JTuple2[java.lang.Long, T]](delegate.elapsed()).map(tuple => (long2Long(tuple._1), tuple._2))
  def elapsed(scheduler: Scheduler): Flux[(Long, T)] = wrapFlux[JTuple2[java.lang.Long, T]](delegate.elapsed()).map(tuple => (long2Long(tuple._1), tuple._2))

  def elementAt(index: Int): Mono[T] = wrapMono[T](delegate.elementAt(index))
  def elementAt(index: Int, defaultValue: T): Mono[T] = wrapMono[T](delegate.elementAt(index, defaultValue))

  def expandDeep(expander: T => Publisher[T]): Flux[T] = wrapFlux[T](delegate.expandDeep(expander))
  def expandDeep(expander: T => Publisher[T], capacityHint: Int): Flux[T] = wrapFlux[T](delegate.expandDeep(expander, capacityHint))

  def expand(expander: T => Publisher[T]): Flux[T] = wrapFlux[T](delegate.expandDeep(expander))
  def expand(expander: T => Publisher[T], capacityHint: Int): Flux[T] = wrapFlux[T](delegate.expandDeep(expander, capacityHint))

  def filter(predicate: T => Boolean): Flux[T] = wrapFlux[T](delegate.filter(predicate))

  def filterWhen(asyncPredicate: T => Publisher[Boolean]): Flux[T] = wrapFlux[T](delegate.filterWhen((t: T) => Flux.from(asyncPredicate(t)).map(boolean2Boolean)))
  def filterWhen(asyncPredicate: T => Publisher[Boolean], bufferSize: Int): Flux[T] = wrapFlux[T](delegate.filterWhen((t: T) => Flux.from(asyncPredicate(t)).map(boolean2Boolean), bufferSize))

  def flatMap[R](mapper: T => Publisher[R]): Flux[R] = wrapFlux[R](delegate.flatMap(mapper))
  def flatMap[V](mapper: T => Publisher[V], concurrency: Int): Flux[V] = wrapFlux[V](delegate.flatMap(mapper, concurrency))
  def flatMap[V](mapper: T => Publisher[V], concurrency: Int, prefetch: Int): Flux[V] = wrapFlux[V](delegate.flatMap(mapper, concurrency, prefetch))
  def flatMapDelayError[V](mapper: T => Publisher[V], concurrency: Int, prefetch: Int): Flux[V] = wrapFlux[V](delegate.flatMapDelayError(mapper, concurrency, prefetch))
  def flatMap[R](mapperOnNext: T => Publisher[R], mapperOnError: Throwable => Publisher[R], mapperOnComplete: () => Publisher[R]): Flux[R] = wrapFlux[R](delegate.flatMap(mapperOnNext, mapperOnError, mapperOnComplete))
  def flatMapIterable[R](mapper: T => Iterable[R]): Flux[R] = wrapFlux[R](delegate.flatMapIterable((t: T) => asJavaIterable(mapper(t))))
  def flatMapIterable[R](mapper: T => Iterable[R], prefetch: Int): Flux[R] = wrapFlux[R](delegate.flatMapIterable((t: T) => asJavaIterable(mapper(t)), prefetch))
  def flatMapSequential[R](mapper: T => Publisher[R]): Flux[R] = wrapFlux[R](delegate.flatMapSequential(mapper))
  def flatMapSequential[R](mapper: T => Publisher[R], maxConcurrency: Int): Flux[R] = wrapFlux[R](delegate.flatMapSequential(mapper, maxConcurrency))
  def flatMapSequential[R](mapper: T => Publisher[R], maxConcurrency: Int, prefetch: Int): Flux[R] = wrapFlux[R](delegate.flatMapSequential(mapper, maxConcurrency, prefetch))
  def flatMapSequentialDelayError[R](mapper: T => Publisher[R], maxConcurrency: Int, prefetch: Int): Flux[R] = wrapFlux[R](delegate.flatMapSequential(mapper, maxConcurrency, prefetch))

  def getPrefetch: Int = delegate.getPrefetch

//  def groupBy[K](keyMapper: T => K): Flux[GroupedFlux[K, T]] // todo add groupedFlux

  def map[V](mapper: T => V): Flux[V] = wrapFlux[V](delegate.map(mapper))

  def subscribe(): Disposable = delegate.subscribe()

  def subscribe(consumer: T => Unit): Disposable = delegate.subscribe(consumer)

  def subscribe(consumer: T => Unit,
                errorConsumer: Throwable => Unit): Disposable = delegate.subscribe(consumer, errorConsumer)

  def subscribe(consumer: T => Unit,
                errorConsumer: Throwable => Unit,
                completeConsumer: () => Unit): Disposable = delegate.subscribe(consumer, errorConsumer, completeConsumer)





  ///
  /// BRIDGE METHODS
  ///

  override def subscribe(s: Subscriber[_ >: T]): Unit = delegate.subscribe(s)

}