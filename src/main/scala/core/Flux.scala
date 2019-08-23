package core

import java.{lang, util}
import java.util.function.{BiFunction, Consumer, Supplier, Function => JFunction}
import java.time.{Duration => JDuration}
import java.util.concurrent.Callable
import java.util.logging.Level
import java.util.stream.Collector

import org.reactivestreams.{Publisher, Subscriber, Subscription}
import reactor.core.{Disposable, publisher}
import reactor.core.publisher.FluxSink.OverflowStrategy
import reactor.core.publisher.{BufferOverflowStrategy, FluxSink, Signal, SignalType, SynchronousSink, Flux => JFlux, Mono => JMono}
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
import reactor.util.Logger
import reactor.util.context.Context
import reactor.util.function.Tuples
import scala.collection.JavaConverters._

import scala.collection.convert.Wrappers.IterableWrapper
import scala.collection.immutable.Queue
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

trait Flux[T] extends Publisher[T] with ImplicitJavaInterop {

  private[core] val delegate: JFlux[T]

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

  def buffer(): Flux[Seq[T]] = wrapFlux(delegate.buffer()).map(toScalaSeq)
  def buffer(maxSize: Int): Flux[Seq[T]] = wrapFlux(delegate.buffer(maxSize)).map(toScalaSeq)
  def buffer(maxSize: Int, skip: Int): Flux[Seq[T]] = wrapFlux(delegate.buffer(maxSize, skip)).map(toScalaSeq)
  def buffer(other: Publisher[T]): Flux[Seq[T]] = wrapFlux(delegate.buffer(other)).map(toScalaSeq)

  def buffer(bufferingTimespan: FiniteDuration): Flux[Seq[T]] = wrapFlux(delegate.buffer(bufferingTimespan)).map(toScalaSeq)
  def buffer(bufferingTimespan: FiniteDuration, openBufferEvery: FiniteDuration): Flux[Seq[T]] = wrapFlux(delegate.buffer(bufferingTimespan, openBufferEvery)).map(toScalaSeq)
  def buffer(bufferingTimespan: FiniteDuration, timer: Scheduler): Flux[Seq[T]] = wrapFlux(delegate.buffer(bufferingTimespan, timer)).map(toScalaSeq)
  def buffer(bufferingTimespan: FiniteDuration, openBufferEvery: FiniteDuration, timer: Scheduler): Flux[Seq[T]] = wrapFlux(delegate.buffer(bufferingTimespan, openBufferEvery, timer)).map(toScalaSeq)

  def bufferTimeout(maxSize: Int, maxTime: FiniteDuration): Flux[Seq[T]] = wrapFlux(delegate.bufferTimeout(maxSize, maxTime)).map(toScalaSeq)
  // todo missing custom collection wrapper here (see JavaInterop's toIterable(collection: Collection) method)
  def bufferTimeout(maxSize: Int, maxTime: FiniteDuration, timer: Scheduler): Flux[Seq[T]] = wrapFlux(delegate.bufferTimeout(maxSize, maxTime, timer)).map(toScalaSeq)
  // todo missing custom collection wrapper here

  def bufferUntil(predicate: T => Boolean): Flux[Seq[T]] = wrapFlux(delegate.bufferUntil(predicate)).map(toScalaSeq)
  def bufferUntil(predicate: T => Boolean, cutBefore: Boolean): Flux[Seq[T]] = wrapFlux(delegate.bufferUntil(predicate, cutBefore)).map(toScalaSeq)

  def bufferWhile(predicate: T => Boolean): Flux[Seq[T]] = wrapFlux(delegate.bufferWhile(predicate)).map(toScalaSeq)

  def bufferWhen[U, V](bucketOpening: Publisher[U], closeSelector: U => Publisher[V]): Flux[Seq[T]] = wrapFlux(delegate.bufferWhen(bucketOpening, closeSelector)).map(toScalaSeq)
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
  def collectSeq(): Mono[Seq[T]] = wrapMono(delegate.collectList()).map(toScalaSeq)

  def collectMap[K](keyExtractor: T => K): Mono[_ <: Map[K, T]] = {
    // this explicit type def is required
    val mono: Mono[util.Map[K, T]] = wrapMono(delegate.collectMap(keyExtractor))
    mono.map(toScalaMap)
  }

  def collectMap[K, V](keyExtractor: T => K, valueExtractor: T => V): Mono[_ <: Map[K, V]] = {
    // this explicit type def is required
    val mono: Mono[util.Map[K, V]] = wrapMono(delegate.collectMap(keyExtractor, valueExtractor))
    mono.map(toScalaMap)
  }

  def collectMap[K, V](keyExtractor: T => K, valueExtractor: T => V, mapSupplier: () => mutable.Map[K, V]): Mono[_ <: Map[K, V]] = {
    // this explicit type def is required
    val mono: Mono[util.Map[K, V]] = wrapMono(delegate.collectMap(keyExtractor, valueExtractor, () => toJavaMutableMap(mapSupplier())))
    mono.map(toScalaMap)
  }

  def collectMultimap[K](keyExtractor: T => K): Mono[_ <: Map[K, Iterable[T]]] = {
    // this explicit type def is required
    val mono: Mono[util.Map[K, util.Collection[T]]] = wrapMono(delegate.collectMultimap(keyExtractor))
    mono.map(toScalaMap).map(map => map.mapValues(toScalaIterable))
  }

  def collectMultimap[K, V](keyExtractor: T => K, valueExtractor: T => V): Mono[_ <: Map[K, Iterable[V]]] = {
    // this explicit type def is required
    val mono: Mono[util.Map[K, util.Collection[V]]] = wrapMono(delegate.collectMultimap(keyExtractor, valueExtractor))
    mono.map(toScalaMap).map(map => map.mapValues(toScalaIterable))
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

  def groupBy[K](keyMapper: T => K): Flux[GroupedFlux[K, T]] = wrapFlux[GroupedFlux[K, T]](delegate.groupBy[K](keyMapper).map(wrapGroupedFlux[K, T](_)))
  def groupBy[K](keyMapper: T => K, prefetch: Int): Flux[GroupedFlux[K, T]] = wrapFlux[GroupedFlux[K, T]](delegate.groupBy[K](keyMapper, prefetch).map(wrapGroupedFlux[K, T](_)))
  def groupBy[K, V](keyMapper: T => K, valueMapper: T => V): Flux[GroupedFlux[K, V]] = wrapFlux[GroupedFlux[K, V]](delegate.groupBy[K, V](keyMapper, valueMapper).map(wrapGroupedFlux[K, V](_)))
  def groupBy[K, V](keyMapper: T => K, valueMapper: T => V, prefetch: Int): Flux[GroupedFlux[K, V]] = wrapFlux[GroupedFlux[K, V]](delegate.groupBy[K, V](keyMapper, valueMapper, prefetch).map(wrapGroupedFlux[K, V](_)))

  def groupJoin[TRight, TLeftEnd, TRightEnd, R](other: Publisher[TRight], leftEnd: T => Publisher[TLeftEnd], rightEnd: TRight => Publisher[TRightEnd], resultSelector: (T, Flux[TRight]) => R): Flux[R] = wrapFlux[R](delegate.groupJoin(other, leftEnd, rightEnd, (t: T, f: JFlux[TRight]) => resultSelector(t, wrapFlux(f))))
  // commented out code is equivalent to the above function
//  def groupJoin[TRight, TLeftEnd, TRightEnd, R](other: Publisher[TRight], leftEnd: T => Publisher[TLeftEnd], rightEnd: TRight => Publisher[TRightEnd], resultSelector: (T, Flux[TRight]) => R): Flux[R] = {
//    val resultSelectorJFlux: java.util.function.BiFunction[T, JFlux[TRight], R] = (t: T, f: JFlux[TRight]) => resultSelector.apply(t, wrapFlux[TRight](f))
//    wrapFlux[R](delegate.groupJoin[TRight, TLeftEnd, TRightEnd, R](other, leftEnd, rightEnd, resultSelectorJFlux))
//  }

  def handle[R](handler: (T, SynchronousSink[R]) => Unit): Flux[R] = wrapFlux[R](delegate.handle(handler))

  def hasElement(value: T): Mono[Boolean] = wrapMono[Boolean](delegate.hasElement(value).map(boolean2Boolean(_)))

  def hasElements: Mono[Boolean] = wrapMono[Boolean](delegate.hasElements.map(boolean2Boolean(_)))

  def hide(): Flux[T] = wrapFlux[T](delegate.hide())

  def index(): Flux[(Long, T)] = wrapFlux[(Long, T)](delegate.index().map(tuple => (long2Long(tuple._1), tuple._2)))
//  def index[I](indexMapper: (Long, T) => I): Flux[I] = wrapFlux[I](delegate.index((l: java.lang.Long, t: T) => indexMapper(long2Long(l), t)))
  def index[I](indexMapper: (Long, T) => I): Flux[I] = wrapFlux[I](delegate.index((l: java.lang.Long, t: T) => indexMapper(l, t))) // todo: need def above with long2Long ?

  def ignoreElements(): Mono[T] = wrapMono[T](delegate.ignoreElements())

  def join[TRight, TLeftEnd, TRightEnd, R](other: Publisher[TRight], leftEnd: T => Publisher[TLeftEnd], rightEnd: TRight => Publisher[TRightEnd], resultSelector: (T, TRight) => R): Flux[R] = wrapFlux[R](delegate.join(other, leftEnd, rightEnd, resultSelector))

  def last: Mono[T] = wrapMono[T](delegate.last())
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

  def map[V](mapper: T => V): Flux[V] = wrapFlux[V](delegate.map(mapper))

  def materialize(): Flux[Signal[T]] = wrapFlux[Signal[T]](delegate.materialize())

  def mergeOrderedWith(other: Publisher[T], ordering: Ordering[T]): Flux[T] = wrapFlux[T](delegate.mergeOrderedWith(other, ordering))
  def mergeWith(other: Publisher[T]): Flux[T] = wrapFlux[T](delegate.mergeWith(other))

  def metrics(): Flux[T] = wrapFlux[T](delegate.metrics())

  def name(name: String): Flux[T] = wrapFlux[T](delegate.name(name))

  def next(): Mono[T] = wrapMono[T](delegate.next())

  def ofType[U](clazz: Class[U]): Flux[U] = wrapFlux[U](delegate.ofType(clazz))

  def onBackpressureBuffer(): Flux[T] = wrapFlux[T](delegate.onBackpressureBuffer())
  def onBackpressureBuffer(maxSize: Int): Flux[T] = wrapFlux[T](delegate.onBackpressureBuffer(maxSize))
  def onBackpressureBuffer(maxSize: Int, onOverflow: T => Unit): Flux[T] = wrapFlux[T](delegate.onBackpressureBuffer(maxSize, onOverflow))
  def onBackpressureBuffer(maxSize: Int, bufferOverflowStrategy: BufferOverflowStrategy): Flux[T] = wrapFlux[T](delegate.onBackpressureBuffer(maxSize, bufferOverflowStrategy))
  def onBackpressureBuffer(maxSize: Int, onOverflow: T => Unit, bufferOverflowStrategy: BufferOverflowStrategy): Flux[T] = wrapFlux[T](delegate.onBackpressureBuffer(maxSize, onOverflow, bufferOverflowStrategy))
  def onBackpressureBuffer(ttl: FiniteDuration, maxSize: Int, onBufferEviction: T => Unit): Flux[T] = wrapFlux[T](delegate.onBackpressureBuffer(ttl, maxSize, onBufferEviction))
  def onBackpressureBuffer(ttl: FiniteDuration, maxSize: Int, onBufferEviction: T => Unit, scheduler: Scheduler): Flux[T] = wrapFlux[T](delegate.onBackpressureBuffer(ttl, maxSize, onBufferEviction, scheduler))

  def onBackpressureDrop(): Flux[T] = wrapFlux[T](delegate.onBackpressureDrop())
  def onBackpressureDrop(onDropped: T => Unit): Flux[T] = wrapFlux[T](delegate.onBackpressureDrop(onDropped))

  def onBackpressureError(): Flux[T] = wrapFlux[T](delegate.onBackpressureError())

  def onBackpressureLatest(): Flux[T] = wrapFlux[T](delegate.onBackpressureLatest())

  def onErrorContinue(errorConsumer: (Throwable, Object) => Unit): Flux[T] = wrapFlux[T](delegate.onErrorContinue(errorConsumer))
  def onErrorContinue[E <: Throwable](errorPredicate: E => Boolean, errorConsumer: (Throwable, Object) => Unit): Flux[T] = wrapFlux[T](delegate.onErrorContinue(errorPredicate, errorConsumer))

  def onErrorStop(): Flux[T] = wrapFlux[T](delegate.onErrorStop())

  def onErrorMap(mapper: Throwable => Throwable): Flux[T] = wrapFlux[T](delegate.onErrorMap(mapper))
  def onErrorMap[E <: Throwable](classType: Class[E], mapper: E => Throwable): Flux[T] = wrapFlux[T](delegate.onErrorMap(classType, mapper))
  def onErrorMap(predicate: Throwable => Boolean, mapper: Throwable => Throwable): Flux[T] = wrapFlux[T](delegate.onErrorMap(mapper))

  def onErrorResume(fallback: Throwable => Publisher[T]): Flux[T] = wrapFlux[T](delegate.onErrorResume(fallback))
  def onErrorResume[E <: Throwable](classType: Class[E], fallback: E => Publisher[T]): Flux[T] = wrapFlux[T](delegate.onErrorResume(classType, fallback))
  def onErrorResume(predicate: Throwable => Boolean, fallback: Throwable => Publisher[T]): Flux[T] = wrapFlux[T](delegate.onErrorResume(predicate, fallback))

  def onErrorReturn(fallbackValue: T): Flux[T] = wrapFlux[T](delegate.onErrorReturn(fallbackValue))
  def onErrorReturn[E <: Throwable](classType: Class[E], fallbackValue: T): Flux[T] = wrapFlux[T](delegate.onErrorReturn(classType, fallbackValue))

  def onTerminateDetach(): Flux[T] = wrapFlux[T](delegate.onTerminateDetach())

  def or(other: Publisher[T]): Flux[T] = wrapFlux[T](delegate.or(other))

  // todo: uncomment these methods after creating a scala ParallelFlux wrapper
//  def parallel(): ParallelFlux[T] = wrapParallelFlux[T](delegate.parallel())
//  def parallel(parallelism: Int): ParallelFlux[T] = wrapParallelFlux[T](delegate.parallel(parallelism))
//  def parallel(): ParallelFlux[T] = wrapParallelFlux[T](delegate.parallel())

  // todo: uncomment these methods after creating a scala ConnectableFlux wrapper
//  def publish(): ConnectableFlux[T] = wrapConnectableFlux[T](delegate.publish())
//  def publish(prefetch: Int): ConnectableFlux[T] = wrapConnectableFlux[T](delegate.publish(prefetch))
//  def publish(transform: Flux[T] => Publisher[R]): ConnectableFlux[T] = wrapConnectableFlux[T](delegate.publish(transform))
//  def publish(transform: Flux[T] => Publisher[R], prefetch: Int): ConnectableFlux[T] = wrapConnectableFlux[T](delegate.publish(transform, prefetch))

  def publishNext(): Mono[T] = wrapMono[T](delegate.publishNext())

  def publishOn(scheduler: Scheduler): Flux[T] = wrapFlux[T](delegate.publishOn(scheduler))
  def publishOn(scheduler: Scheduler, prefetch: Int): Flux[T] = wrapFlux[T](delegate.publishOn(scheduler, prefetch))
  def publishOn(scheduler: Scheduler, delayError: Boolean, prefetch: Int): Flux[T] = wrapFlux[T](delegate.publishOn(scheduler, delayError, prefetch))

  def reduce(aggregator: (T, T) => T): Mono[T] = wrapMono[T](delegate.reduce(aggregator))
  def reduce[A](initial: A, accumulator: (A, T) => A): Mono[A] = wrapMono[A](delegate.reduce[A](initial, accumulator))
  def reduceWith[A](initial: () => A, accumulator: (A, T) => A): Mono[A] = wrapMono[A](delegate.reduceWith[A](initial, accumulator))

  def repeat(): Flux[T] = wrapFlux[T](delegate.repeat())
  def repeat(predicate: () => Boolean): Flux[T] = wrapFlux[T](delegate.repeat(predicate))
  def repeat(numRepeat: Long): Flux[T] = wrapFlux[T](delegate.repeat(numRepeat))
  def repeat(numRepeat: Long, predicate: () => Boolean): Flux[T] = wrapFlux[T](delegate.repeat(numRepeat, predicate))
  def repeatWhen(repeatFactory: Flux[Long] => Publisher[_]): Flux[T] = wrapFlux[T](delegate.repeatWhen((flux: JFlux[lang.Long]) => wrapFlux(flux).map(long2Long(_))))

//  def replay(): ConnectableFlux[T] = wrapConnectableFlux[T](delegate.replay())
//  def replay(history: Int): ConnectableFlux[T] = wrapConnectableFlux[T](delegate.replay(history))
//  def replay(ttl: FiniteDuration): ConnectableFlux[T] = wrapConnectableFlux[T](delegate.replay(ttl))
//  def replay(history: Int, ttl: FiniteDuration): ConnectableFlux[T] = wrapConnectableFlux[T](delegate.replay(history, ttl))
//  def replay(ttl: FiniteDuration, timer: Scheduler): ConnectableFlux[T] = wrapConnectableFlux[T](delegate.replay(ttl, timer))
//  def replay(history: Int, ttl: Duration, timer: Scheduler): ConnectableFlux[T] = wrapConnectableFlux[T](delegate.replay(history, ttl, timer))

  def retry(): Flux[T] = wrapFlux[T](delegate.retry())
  def retry(numRetries: Long): Flux[T] = wrapFlux[T](delegate.retry(numRetries))
  def retry(retryMatcher: Throwable => Boolean): Flux[T] = wrapFlux[T](delegate.retry(retryMatcher))
  def retry(numRetries: Long, retryMatcher: Throwable => Boolean): Flux[T] = wrapFlux[T](delegate.retry(numRetries, retryMatcher))
  def retryWhen(whenFactory: Flux[Throwable] => Publisher[_]): Flux[T] = wrapFlux[T](delegate.retryWhen((flux: JFlux[Throwable]) => whenFactory(wrapFlux(flux))))

  def retryBackoff(numRetries: Long, firstBackoff: FiniteDuration): Flux[T] = wrapFlux[T](delegate.retryBackoff(numRetries, firstBackoff))
  def retryBackoff(numRetries: Long, firstBackoff: FiniteDuration, maxBackoff: FiniteDuration): Flux[T] = wrapFlux[T](delegate.retryBackoff(numRetries, firstBackoff, maxBackoff))
  def retryBackoff(numRetries: Long, firstBackoff: FiniteDuration, maxBackoff: FiniteDuration, backoffScheduler: Scheduler): Flux[T] = wrapFlux[T](delegate.retryBackoff(numRetries, firstBackoff, maxBackoff, backoffScheduler))
  def retryBackoff(numRetries: Long, firstBackoff: FiniteDuration, maxBackoff: FiniteDuration, jitterFactor: Double): Flux[T] = wrapFlux[T](delegate.retryBackoff(numRetries, firstBackoff, maxBackoff, jitterFactor))
  def retryBackoff(numRetries: Long, firstBackoff: FiniteDuration, maxBackoff: FiniteDuration, jitterFactor: Double, backoffScheduler: Scheduler): Flux[T] = wrapFlux[T](delegate.retryBackoff(numRetries, firstBackoff, maxBackoff, jitterFactor, backoffScheduler))

  def sample(timespan: FiniteDuration): Flux[T] = wrapFlux[T](delegate.sample(timespan))
  def sample[U](sampler: Publisher[U]): Flux[T] = wrapFlux[T](delegate.sample(sampler))

  def sampleFirst(timespan: FiniteDuration): Flux[T] = wrapFlux[T](delegate.sampleFirst(timespan))
  def sampleFirst[U](samplerFactory: T => Publisher[U]): Flux[T] = wrapFlux[T](delegate.sampleFirst(samplerFactory))

  def sampleTimeout[U](throttlerFactory: T => Publisher[U]): Flux[T] = wrapFlux[T](delegate.sampleTimeout(throttlerFactory))
  def sampleTimeout[U](throttlerFactory: T => Publisher[U], maxConcurrency: Int): Flux[T] = wrapFlux[T](delegate.sampleTimeout(throttlerFactory, maxConcurrency))

  def scan(accumulator: (T, T) => T): Flux[T] = wrapFlux[T](delegate.scan(accumulator))
  def scan[A](initial: A, accumulator: (A, T) => A): Flux[A] = wrapFlux[A](delegate.scan(initial, accumulator))
  def scanWith[A](initial: () => A, accumulator: (A, T) => A): Flux[A] = wrapFlux[A](delegate.scanWith(initial, accumulator))

  def share(): Flux[T] = wrapFlux[T](delegate.share())

  def single(): Mono[T] = wrapMono[T](delegate.single())
  def single(defaultValue: T): Mono[T] = wrapMono[T](delegate.single(defaultValue))
  def singleOrEmpty(): Mono[T] = wrapMono[T](delegate.singleOrEmpty())

  def skip(skipped: Long): Flux[T] = wrapFlux[T](delegate.skip(skipped))
  def skip(timespan: FiniteDuration): Flux[T] = wrapFlux[T](delegate.skip(timespan))
  def skip(timespan: FiniteDuration, timer: Scheduler): Flux[T] = wrapFlux[T](delegate.skip(timespan, timer))
  def skipLast(n: Int): Flux[T] = wrapFlux[T](delegate.skipLast(n))
  def skipUntil(untilPredicate: T => Boolean): Flux[T] = wrapFlux[T](delegate.skipUntil(untilPredicate))
  def skipUntilOther(other: Publisher[_]): Flux[T] = wrapFlux[T](delegate.skipUntilOther(other))
  def skipWhile(skipPredicate: T => Boolean): Flux[T] = wrapFlux[T](delegate.skipWhile(skipPredicate))

  def sort(): Flux[T] = wrapFlux[T](delegate.sort())
  def sort(ordering: Ordering[T]): Flux[T] = wrapFlux[T](delegate.sort(ordering))

  def startWith(iterable: Iterable[T]): Flux[T] = wrapFlux[T](delegate.startWith(iterable))
  def startWith(values: T*): Flux[T] = wrapFlux[T](delegate.startWith(values:_*))
  def startWith(publisher: Publisher[T]): Flux[T] = wrapFlux[T](delegate.startWith(publisher))


  // todo add optional alternatives or extra overloads to subscribe(...)? note these are nullable values in public api
  def subscribe(): Disposable = delegate.subscribe()
  def subscribe(consumer: T => Unit): Disposable = delegate.subscribe(consumer)
  def subscribe(consumer: T => Unit, errorConsumer: Throwable => Unit): Disposable = delegate.subscribe(consumer, errorConsumer)
  def subscribe(consumer: T => Unit, errorConsumer: Throwable => Unit, completeConsumer: () => Unit): Disposable = delegate.subscribe(consumer, errorConsumer, completeConsumer)

  ///
  /// BRIDGE METHOD Subscribe
  /// (if unsure, it's almost never correct to call this method directly!)
  ///
  override def subscribe(s: Subscriber[_ >: T]): Unit = delegate.subscribe(s)
  ///
  ///
  ///

  def subscriberContext(mergeContext: Context): Flux[T] = wrapFlux[T](delegate.subscriberContext(mergeContext))
  def subscriberContext(doOnContext: Context => Context): Flux[T] = wrapFlux[T](delegate.subscriberContext(doOnContext))

  def subscribeOn(scheduler: Scheduler): Flux[T] = wrapFlux[T](delegate.subscribeOn(scheduler))
  def subscribeOn(scheduler: Scheduler, requestOnSeparateThread: Boolean): Flux[T] = wrapFlux[T](delegate.subscribeOn(scheduler, requestOnSeparateThread))

  def subscribeWith[E <: Subscriber[T]](subscriber: E): E = delegate.subscribeWith(subscriber)

  def switchOnFirst[V](transformer: (Signal[T], Flux[T]) => Publisher[V]): Flux[V] = {
    val f: BiFunction[Signal[T], JFlux[T], Publisher[V]] = asJavaFn2[Signal[T], JFlux[T], Publisher[V]]((s: Signal[T], flux: JFlux[T]) => transformer(s, wrapFlux[T](flux)))
    wrapFlux[V](delegate.switchOnFirst[V](f.asInstanceOf[BiFunction[Signal[_ <: T], JFlux[T], Publisher[_ <: V]]]))
  }

  def switchIfEmpty(alternate: Publisher[T]): Flux[T] = wrapFlux[T](delegate.switchIfEmpty(alternate))

  def switchMap[V](fn: T => Publisher[V]): Flux[V] = wrapFlux[V](delegate.switchMap[V](fn))
  def switchMap[V](fn: T => Publisher[V], prefetch: Int): Flux[V] = wrapFlux[V](delegate.switchMap[V](fn, prefetch))

  def tag(key: String, value: String): Flux[T] = wrapFlux[T](delegate.tag(key, value))

  def take(n: Long): Flux[T] = wrapFlux[T](delegate.take(n))
  def take(timespan: FiniteDuration): Flux[T] = wrapFlux[T](delegate.take(timespan))
  def take(timespan: FiniteDuration, scheduler: Scheduler): Flux[T] = wrapFlux[T](delegate.take(timespan, scheduler))

  def takeLast(n: Int): Flux[T] = wrapFlux[T](delegate.takeLast(n))

  def takeUntil(predicate: T => Boolean): Flux[T] = wrapFlux[T](delegate.takeUntil(predicate))
  def takeUntilOther(other: Publisher[_]): Flux[T] = wrapFlux[T](delegate.takeUntilOther(other))

  def takeWhile(continuePredicate: T => Boolean): Flux[T] = wrapFlux[T](delegate.takeWhile(continuePredicate))

  def thenEmpty(other: Publisher[Unit]): Mono[Unit] = wrapMono[Unit](delegate.thenEmpty(Flux.from(other).map[Void](_ => null: Void)).map(_ => Unit))

  def thenMany[V](other: Publisher[V]): Flux[V] = wrapFlux[V](delegate.thenMany(other))

  def timeout(timeout: FiniteDuration): Flux[T] = wrapFlux[T](delegate.timeout(timeout))
  def timeout(timeout: FiniteDuration, fallback: Publisher[T]): Flux[T] = wrapFlux[T](delegate.timeout(timeout, fallback))
  def timeout(timeout: FiniteDuration, timer: Scheduler): Flux[T] = wrapFlux[T](delegate.timeout(timeout, timer))
  def timeout(timeout: FiniteDuration, fallback: Publisher[T], timer: Scheduler): Flux[T] = wrapFlux[T](delegate.timeout(timeout, fallback, timer))
  def timeout[U](firstTimeout: Publisher[U]): Flux[T] = wrapFlux[T](delegate.timeout[U](firstTimeout))
  def timeout[U, V](firstTimeout: Publisher[U], nextTimeoutFactory: T => Publisher[V]): Flux[T] = wrapFlux[T](delegate.timeout[U, V](firstTimeout, nextTimeoutFactory))
  def timeout[U, V](firstTimeout: Publisher[U], nextTimeoutFactory: T => Publisher[V], fallback: Publisher[T]): Flux[T] = wrapFlux[T](delegate.timeout[U, V](firstTimeout, nextTimeoutFactory, fallback))

  def timestamp(): Flux[(Long, T)] = wrapFlux[(Long, T)](delegate.timestamp().map(t => (long2Long(t._1), t._2)))
  def timestamp(scheduler: Scheduler): Flux[(Long, T)] = wrapFlux[(Long, T)](delegate.timestamp(scheduler).map(t => (long2Long(t._1), t._2)))

  def toIterable: Iterable[T] = toScalaIterable(delegate.toIterable())
  def toIterable(batchSize: Int): Iterable[T] = toScalaIterable(delegate.toIterable(batchSize))
  def toIterable(batchSize: Int, queueProvider: () => util.Queue[T]): Iterable[T] = toScalaIterable(delegate.toIterable(batchSize, queueProvider))

  // toSeq is not part of Java api. Added for convenience.
  def toSeq: Iterable[T] = toScalaIterable(delegate.toIterable()).toSeq
  def toSeq(batchSize: Int): Iterable[T] = toScalaIterable(delegate.toIterable(batchSize)).toSeq

  def toStream: Stream[T] = delegate.toStream().iterator().asScala.toStream
  def toStream(batchSize: Int): Stream[T] = delegate.toStream(batchSize).iterator().asScala.toStream

  def transform[V](transformer: Flux[T] => Publisher[V]): Flux[V] = wrapFlux(delegate.transform[V](asJavaFn1((jflux: JFlux[T]) => transformer(wrapFlux(jflux)))))
  def transformDeferred[V](transformer: Flux[T] => Publisher[V]): Flux[V] = wrapFlux(delegate.transformDeferred[V](asJavaFn1((jflux: JFlux[T]) => transformer(wrapFlux(jflux)))))

  def window(maxSize: Int): Flux[Flux[T]] = wrapFlux[Flux[T]](delegate.window(maxSize).map(wrapFlux[T](_)))
  def window(maxSize: Int, skip: Int): Flux[Flux[T]] = wrapFlux[Flux[T]](delegate.window(maxSize, skip).map(wrapFlux[T](_)))
  def window(boundary: Publisher[_]): Flux[Flux[T]] = wrapFlux[Flux[T]](delegate.window(boundary).map(wrapFlux[T](_)))
  def window(windowingTimespan: FiniteDuration): Flux[Flux[T]] = wrapFlux[Flux[T]](delegate.window(windowingTimespan).map(wrapFlux[T](_)))
  def window(windowingTimespan: FiniteDuration, openWindowEvery: FiniteDuration): Flux[Flux[T]] = wrapFlux[Flux[T]](delegate.window(windowingTimespan, openWindowEvery).map(wrapFlux[T](_)))
  def window(windowingTimespan: FiniteDuration, timer: Scheduler): Flux[Flux[T]] = wrapFlux[Flux[T]](delegate.window(windowingTimespan, timer).map(wrapFlux[T](_)))
  def window(windowingTimespan: FiniteDuration, openWindowEvery: FiniteDuration, timer: Scheduler): Flux[Flux[T]] = wrapFlux[Flux[T]](delegate.window(windowingTimespan, openWindowEvery, timer).map(wrapFlux[T](_)))

  def windowTimeout(maxSize: Int, maxTime: FiniteDuration): Flux[Flux[T]] = wrapFlux[Flux[T]](delegate.windowTimeout(maxSize, maxTime).map(wrapFlux[T](_)))
  def windowTimeout(maxSize: Int, maxTime: FiniteDuration, timer: Scheduler): Flux[Flux[T]] = wrapFlux[Flux[T]](delegate.windowTimeout(maxSize, maxTime, timer).map(wrapFlux[T](_)))

  def windowUntil(boundaryTrigger: T => Boolean): Flux[Flux[T]] = wrapFlux[Flux[T]](delegate.windowUntil(boundaryTrigger).map(wrapFlux[T](_)))
  def windowUntil(boundaryTrigger: T => Boolean, cutBefore: Boolean): Flux[Flux[T]] = wrapFlux[Flux[T]](delegate.windowUntil(boundaryTrigger, cutBefore).map(wrapFlux[T](_)))
  def windowUntil(boundaryTrigger: T => Boolean, cutBefore: Boolean, prefetch: Int): Flux[Flux[T]] = wrapFlux[Flux[T]](delegate.windowUntil(boundaryTrigger, cutBefore, prefetch).map(wrapFlux[T](_)))

  def windowWhile(inclusionPredicate: T => Boolean): Flux[Flux[T]] = wrapFlux[Flux[T]](delegate.windowWhile(inclusionPredicate).map(wrapFlux[T](_)))
  def windowWhile(inclusionPredicate: T => Boolean, prefetch: Int): Flux[Flux[T]] = wrapFlux[Flux[T]](delegate.windowWhile(inclusionPredicate, prefetch).map(wrapFlux[T](_)))

  def windowWhen[U, V](bucketOpening: Publisher[U], closeSelector: U => Publisher[V]): Flux[Flux[T]] = wrapFlux[Flux[T]](delegate.windowWhen(bucketOpening, closeSelector).map(wrapFlux[T](_)))

  def withLatestFrom[U, R](other: Publisher[U], resultSelector: (T, U) => R): Flux[R] = wrapFlux[R](delegate.withLatestFrom(other, resultSelector))

  def zipWith[T2](source2: Publisher[T2]): Flux[(T, T2)] = wrapFlux[(T, T2)](delegate.zipWith(source2).map(toScalaTuple2(_)))
  def zipWith[T2](source2: Publisher[T2], prefetch: Int): Flux[(T, T2)] = wrapFlux[(T, T2)](delegate.zipWith(source2, prefetch).map(toScalaTuple2(_)))
  def zipWith[T2, V](source2: Publisher[T2], combinator: (T, T2) => V): Flux[V] = wrapFlux[V](delegate.zipWith(source2, combinator))
  def zipWith[T2, V](source2: Publisher[T2], prefetch: Int, combinator: (T, T2) => V): Flux[V] = wrapFlux[V](delegate.zipWith(source2, prefetch, combinator))

  def zipWithIterable[T2](iterable: Iterable[T2]): Flux[(T, T2)] = wrapFlux[(T, T2)](delegate.zipWithIterable(iterable).map(toScalaTuple2(_)))
  def zipWithIterable[T2, V](iterable: Iterable[T2], zipper: (T, T2) => V): Flux[V] = wrapFlux[V](delegate.zipWithIterable(iterable, zipper))


}

private[core] class FluxImpl[T](publisher: Publisher[T]) extends Flux[T] {
  override private[core] val delegate = JFlux.from(publisher)
}




























