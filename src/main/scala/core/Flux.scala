package core

import java.lang
import java.util.function.Consumer
import java.time.{Duration => JDuration}
import java.util.concurrent.Callable

import org.reactivestreams.{Publisher, Subscriber}
import reactor.core.{Disposable, JFluxVarargs, publisher}
import reactor.core.publisher.FluxSink.OverflowStrategy
import reactor.core.publisher.{FluxSink, SynchronousSink}
import reactor.test.StepVerifier
import reactor.core.publisher.{Flux => JFlux}
import reactor.test.scheduler.VirtualTimeScheduler
import reactor.util.concurrent.Queues.{SMALL_BUFFER_SIZE, XS_BUFFER_SIZE}
import core.JavaInterop._
import java.util.function.{Function => JFunction}

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

import scala.concurrent.duration.{Duration, FiniteDuration}
import scala.concurrent.duration.Duration.Infinite
import scala.language.{existentials, higherKinds}

object Flux extends ImplicitJavaInterop {

  ///
  /// API METHODS
  ///

  def combineLatest[T, V](combinator: Array[AnyRef] => V, sources: Publisher[T]*): Flux[V] = wrap(JFlux.combineLatest(combinator, sources:_*))
  def combineLatest[T, V](combinator: Array[AnyRef] => V, prefetch: Int, sources: Publisher[T]*): Flux[V] = wrap(JFlux.combineLatest(combinator, prefetch, sources:_*))
  def combineLatest[T1, T2, V](source1: Publisher[T1], source2: Publisher[T2], combinator: (_ >: T1, _ >: T2) => _ <: V): Flux[V] = wrap(JFlux.combineLatest(source1, source2, combinator))
  def combineLatest[T1 >: Any, T2 >: Any, T3 >: Any, V <: Any](source1: Publisher[T1], source2: Publisher[T2], source3: Publisher[T3], combinator: (_ >: T1, _ >: T2, _ >: T3) => V): Flux[V] = wrap(JFlux.combineLatest(source1, source2, source3, combinator))
  def combineLatest[T1 >: Any, T2 >: Any, T3 >: Any, T4 >: Any, V <: Any](source1: Publisher[T1], source2: Publisher[T2], source3: Publisher[T3], source4: Publisher[T4], combinator: (_ >: T1, _ >: T2, _ >: T3, _ >: T4) => V): Flux[V] = wrap(JFlux.combineLatest(source1, source2, source3, source4, combinator))
  def combineLatest[T1 >: Any, T2 >: Any, T3 >: Any, T4 >: Any, T5 >: Any, V <: Any](source1: Publisher[T1], source2: Publisher[T2], source3: Publisher[T3], source4: Publisher[T4], source5: Publisher[T5], combinator: (_ >: T1, _ >: T2, _ >: T3, _ >: T4, _ >: T5) => V): Flux[V] = wrap(JFlux.combineLatest(source1, source2, source3, source4, source5, combinator))
  def combineLatest[T1 >: Any, T2 >: Any, T3 >: Any, T4 >: Any, T5 >: Any, T6 >: Any, V <: Any](source1: Publisher[T1], source2: Publisher[T2], source3: Publisher[T3], source4: Publisher[T4], source5: Publisher[T5], source6: Publisher[T6], combinator: (_ >: T1, _ >: T2, _ >: T3, _ >: T4, _ >: T5, _ >: T6) => V): Flux[V] = wrap(JFlux.combineLatest(source1, source2, source3, source4, source5, source6, combinator))
  def combineLatest[T, V](sources: Iterable[_ <: Publisher[T]], combinator: Array[AnyRef] => V): Flux[V] = wrap(JFlux.combineLatest(sources, combinator))
  def combineLatest[T, V](sources: Iterable[_ <: Publisher[T]], prefetch: Int, combinator: Array[AnyRef] => V): Flux[V] = wrap(JFlux.combineLatest(sources, prefetch, combinator))

  def concat[T](sources: Iterable[_ <: Publisher[T]]): Flux[T] = wrap(JFlux.concat(sources))
  def concatWithValues[T](sources: Publisher[T]*): Flux[T] = wrap(JFlux.concat(sources:_*))
  def concat[T](sources: Publisher[_ <: Publisher[T]]): Flux[T] = wrap(JFlux.concat(sources))
  def concat[T](sources: Publisher[_ <: Publisher[T]], prefetch: Int): Flux[T] = wrap(JFlux.concat(sources, prefetch))
  def concat[T](sources: Publisher[T]*): Flux[T] = wrap(JFlux.concat(sources:_*))

  def concatDelayError[T](sources: Publisher[_ <: Publisher[T]]): Flux[T] = wrap(JFlux.concatDelayError(sources))
  def concatDelayError[T](sources: Publisher[_ <: Publisher[T]], prefetch: Int): Flux[T] = wrap(JFlux.concatDelayError(sources, prefetch))
  def concatDelayError[T](sources: Publisher[_ <: Publisher[T]], delayUntilEnd: Boolean, prefetch: Int): Flux[T] = wrap(JFlux.concatDelayError(sources, delayUntilEnd, prefetch))
  def concatDelayError[T](sources: Publisher[T]*): Flux[T] = wrap(JFlux.concatDelayError(sources:_*))

  def create[T](emitter: FluxSink[T] => Unit): Flux[T] = wrap(JFlux.create(emitter))
  def create[T](emitter: FluxSink[T] => Unit, backpressure: OverflowStrategy): Flux[T] = wrap(JFlux.create(emitter, backpressure))

  def push[T](emitter: FluxSink[T] => Unit): Flux[T] = wrap(JFlux.create(emitter))
  def push[T](emitter: FluxSink[T] => Unit, backpressure: OverflowStrategy): Flux[T] = wrap(JFlux.create(emitter, backpressure))

  def defer[T](supplier: () => Publisher[T]): Flux[T] = wrap(JFlux.defer(supplier))
  def deferWithContext[T](supplier: Context => Publisher[T]): Flux[T] = wrap(JFlux.deferWithContext(supplier))

  def empty[T](): Flux[T] = wrap(JFlux.empty())

  def error[T](error: Throwable): Flux[T] = wrap(JFlux.error(error))
  def error[T](errorSupplier: () => Throwable): Flux[T] = wrap(JFlux.error(errorSupplier))
  def error[T](error: Throwable, whenRequested: Boolean): Flux[T] = wrap(JFlux.error(error))

  def first[T](sources: Publisher[T]*): Flux[T] = wrap(JFlux.first(sources:_*))
//  def first[T](sources: Iterable[_ <: Publisher[T]]): Flux[T] = wrap(JFlux.first(sources))

  def from[T](source: Publisher[T]): Flux[T] = wrap(JFlux.from(source))
  def fromArray[T >: AnyRef](source: Array[T with AnyRef]): Flux[T] = wrap(JFlux.fromArray(source))
  def fromIterable[T](source: Iterable[T]): Flux[T] = wrap(JFlux.fromIterable(source))

  // scala stream can't directly map to java stream, so ignore this (since fromIterable will work with this)
//  def fromStream[T](stream: Stream[T]): Flux[T] = wrap(JFlux.fromStream(stream))
  def fromStream[T](stream: Stream[T]): Flux[T] = wrap(JFlux.fromIterable(stream))
//  def fromStream[T](streamSupplier: () => Stream[T]): Flux[T] = wrap(JFlux.fromStream(streamSupplier))

  def generate[T](generator: SynchronousSink[T] => Unit): Flux[T] = wrap(JFlux.generate(generator))
  def generate[T, S](stateSupplier: Callable[S], generator: (S, SynchronousSink[T]) => S): Flux[T] = wrap(JFlux.generate(stateSupplier, generator))
  def generate[T, S](stateSupplier: Callable[S], generator: (S, SynchronousSink[T]) => S, stateConsumer: S => Unit): Flux[T] = wrap(JFlux.generate(stateSupplier, generator, stateConsumer))


  def interval[T, S](period: Duration): Flux[Long] = period match {
    case _: Infinite => wrap(JFlux.never())
    case finiteDuration: FiniteDuration => wrap(JFlux.interval(finiteDuration).map(Long2long))
  }

  def interval[T, S](delay: Duration, period: Duration): Flux[Long] = (delay, period) match {
    case (_: Infinite, _: Infinite) => wrap(JFlux.never())
    case (_: Infinite, period: FiniteDuration) => wrap(JFlux.never())
    case (delay: FiniteDuration, _: Infinite) => ??? // todo implement once more timing methods added
    case (delay: FiniteDuration, period: FiniteDuration) => wrap(JFlux.interval(delay, period).map(Long2long))
  }

  def interval[T, S](period: Duration, timer: Scheduler): Flux[Long] = period match {
    case _: Infinite => wrap(JFlux.never())
    case finiteDuration: FiniteDuration => wrap(JFlux.interval(finiteDuration, timer).map(Long2long))
  }

  def interval[T, S](delay: Duration, period: Duration, timer: Scheduler): Flux[Long] = (delay, period) match {
    case (_: Infinite, _: Infinite) => wrap(JFlux.never())
    case (_: Infinite, period: FiniteDuration) => wrap(JFlux.never())
    case (delay: FiniteDuration, _: Infinite) => ??? // todo implement once more timing methods added
    case (delay: FiniteDuration, period: FiniteDuration) => wrap(JFlux.interval(delay, period, timer).map(Long2long))
  }

  def just[T](data: T*): Flux[T] = wrap(JFlux.just(data:_*))
//  def just[T](data: T): Flux[T] = wrap(JFlux.just(data))

  def merge[T](source: Publisher[Publisher[T]]): Flux[T] = wrap(JFlux.merge(source))
  def merge[T](source: Publisher[Publisher[T]], concurrency: Int): Flux[T] = wrap(JFlux.merge(source, concurrency))
  def merge[T](source: Publisher[Publisher[T]], concurrency: Int, prefetch: Int): Flux[T] = wrap(JFlux.merge(source, concurrency, prefetch))
  def merge[T](sources: Iterable[Publisher[T]], concurrency: Int, prefetch: Int): Flux[T] = wrap(JFlux.merge(sources))
  def merge[T](sources: Publisher[T]*): Flux[T] = wrap(JFlux.merge(sources:_*))
  def merge[T](prefetch: Int, sources: Publisher[T]*): Flux[T] = wrap(JFlux.merge(sources:_*))

  def mergeDelayError[T](prefetch: Int, sources: Publisher[T]): Flux[T] = wrap(JFlux.mergeDelayError(prefetch, sources))

  // todo reason to have T in publisher? compiles either way and reduces freedom?
  def mergeOrdered[T <: Ordered[T]](sources: Publisher[T]): Flux[T] = wrap(JFlux.mergeOrdered(sources))
  def mergeOrdered[T](ordering: Ordering[T], sources: Publisher[T]): Flux[T] = wrap(JFlux.mergeOrdered(ordering, sources))
  def mergeOrdered[T](prefetch: Int, ordering: Ordering[T], sources: Publisher[T]): Flux[T] = wrap(JFlux.mergeOrdered(prefetch, ordering, sources))

  def mergeSequential[T](sources: Publisher[_ <: Publisher[T]]): Flux[T] = wrap(JFlux.mergeSequential(sources))
  def mergeSequential[T](sources: Publisher[_ <: Publisher[T]], maxConcurrency: Int, prefetch: Int): Flux[T] = wrap(JFlux.mergeSequential(sources, maxConcurrency, prefetch))
  def mergeSequentialDelayError[T](sources: Publisher[_ <: Publisher[T]], maxConcurrency: Int, prefetch: Int): Flux[T] = wrap(JFlux.mergeSequentialDelayError(sources, maxConcurrency, prefetch))
  def mergeSequential[T](sources: Publisher[T]*): Flux[T] = wrap(JFlux.mergeSequential(sources:_*))
  def mergeSequential[T](prefetch: Int, sources: Publisher[T]*): Flux[T] = wrap(JFlux.mergeSequential(prefetch, sources:_*))
  def mergeSequentialDelayError[T](prefetch: Int, sources: Publisher[T]*): Flux[T] = wrap(JFlux.mergeSequentialDelayError(prefetch, sources:_*))
  def mergeSequential[T](sources: Iterable[_ <: Publisher[T]]): Flux[T] = wrap(JFlux.mergeSequential(sources))
  def mergeSequential[T](sources: Iterable[_ <: Publisher[T]], maxConcurrency: Int, prefetch: Int): Flux[T] = wrap(JFlux.mergeSequential(sources, maxConcurrency, prefetch))
  def mergeSequentialDelayError[T](sources: Iterable[_ <: Publisher[T]], maxConcurrency: Int, prefetch: Int): Flux[T] = wrap(JFlux.mergeSequentialDelayError(sources, maxConcurrency, prefetch))

  def never[T](): Flux[T] = wrap(JFlux.never())

  def range(start: Int, count: Int): Flux[Int] = wrap(JFlux.range(start, count).map(_.toInt))

  def switchOnNext[T](mergedPublishers: Publisher[_ <: Publisher[T]]): Flux[T] = wrap(JFlux.switchOnNext(mergedPublishers))
  def switchOnNext[T](mergedPublishers: Publisher[_ <: Publisher[T]], prefetch: Int): Flux[T] = wrap(JFlux.switchOnNext(mergedPublishers, prefetch))

  def using[T, D](resourceSupplier: Callable[D], sourceSupplier: D => Publisher[T], resourceCleanup: D => Unit): Flux[T] = wrap(JFlux.using(resourceSupplier, sourceSupplier, resourceCleanup))
  def using[T, D](resourceSupplier: Callable[D], sourceSupplier: D => Publisher[T], resourceCleanup: D => Unit, eager: Boolean): Flux[T] = wrap(JFlux.using(resourceSupplier, sourceSupplier, resourceCleanup, eager))

  def usingWhen[T, D](resourceSupplier: Publisher[D], resourceClosure: D => Publisher[T], asyncComplete: D => Publisher[_], asyncError: D => Publisher[_]): Flux[T] = wrap(JFlux.usingWhen(resourceSupplier, resourceClosure, asyncComplete, asyncError))
  def usingWhen[T, D](resourceSupplier: Publisher[D], resourceClosure: D => Publisher[T], asyncComplete: D => Publisher[_], asyncError: D => Publisher[_], asyncCancel: D => Publisher[_]): Flux[T] = wrap(JFlux.usingWhen(resourceSupplier, resourceClosure, asyncComplete, asyncError, asyncCancel))
  def usingWhen[T, D](resourceSupplier: Publisher[D], resourceClosure: D => Publisher[T], asyncCleanup: D => Publisher[_]): Flux[T] = wrap(JFlux.usingWhen(resourceSupplier, resourceClosure, asyncCleanup))

  def zip[T1, T2, O](source1: Publisher[T1], source2: Publisher[T2], combinator: (T1, T2) => O): Flux[O] = wrap(JFlux.zip(source1, source2, combinator))

  def zip[T1, T2](source1: Publisher[T1], source2: Publisher[T2]): Flux[(T1, T2)] = wrap(JFlux.zip(source1, source2).map(toScalaTuple2(_)))
  def zip[T1, T2, T3](source1: Publisher[T1], source2: Publisher[T2], source3: Publisher[T3]): Flux[(T1, T2, T3)] = wrap(JFlux.zip(source1, source2, source3).map(toScalaTuple3(_)))
  def zip[T1, T2, T3, T4](source1: Publisher[T1], source2: Publisher[T2], source3: Publisher[T3], source4: Publisher[T4]): Flux[(T1, T2, T3, T4)] = wrap(JFlux.zip(source1, source2, source3, source4).map(toScalaTuple4(_)))
  def zip[T1, T2, T3, T4, T5](source1: Publisher[T1], source2: Publisher[T2], source3: Publisher[T3], source4: Publisher[T4], source5: Publisher[T5]): Flux[(T1, T2, T3, T4, T5)] = wrap(JFlux.zip(source1, source2, source3, source4, source5).map(toScalaTuple5(_)))
  def zip[T1, T2, T3, T4, T5, T6](source1: Publisher[T1], source2: Publisher[T2], source3: Publisher[T3], source4: Publisher[T4], source5: Publisher[T5], source6: Publisher[T6]): Flux[(T1, T2, T3, T4, T5, T6)] = wrap(JFlux.zip(source1, source2, source3, source4, source5, source6).map(toScalaTuple6(_)))
  def zip[T1, T2, T3, T4, T5, T6, T7](source1: Publisher[T1], source2: Publisher[T2], source3: Publisher[T3], source4: Publisher[T4], source5: Publisher[T5], source6: Publisher[T6], source7: Publisher[T7]): Flux[(T1, T2, T3, T4, T5, T6, T7)] = wrap(JFlux.zip(source1, source2, source3, source4, source5, source6, source7).map(toScalaTuple7(_)))
  def zip[T1, T2, T3, T4, T5, T6, T7, T8](source1: Publisher[T1], source2: Publisher[T2], source3: Publisher[T3], source4: Publisher[T4], source5: Publisher[T5], source6: Publisher[T6], source7: Publisher[T7], source8: Publisher[T8]): Flux[(T1, T2, T3, T4, T5, T6, T7, T8)] = wrap(JFlux.zip(source1, source2, source3, source4, source5, source6, source7, source8).map(toScalaTuple8(_)))

  def zip[O](sources: Iterable[Publisher[_]], combinator: (_ >: Array[AnyRef]) => O): Flux[O] = wrap(JFlux.zip(sources, combinator))
  def zip[O](sources: Iterable[Publisher[_]], prefetch: Int, combinator: (_ >: Array[AnyRef]) => O): Flux[O] = wrap(JFlux.zip(sources, prefetch, combinator))

  def zip[I, O](combinator: (_ >: Array[AnyRef]) => O, sources: Publisher[I]*): Flux[O] = wrap(JFlux.zip(combinator, sources:_*))
  def zip[I, O](combinator: (_ >: Array[AnyRef]) => O, prefetch: Int, sources: Publisher[I]*): Flux[O] = wrap(JFlux.zip(combinator, prefetch, sources:_*))

  def zip[V](sources: Publisher[_ <: Publisher[_]], combinator: (Any, Any) => V): Flux[V] = wrap(JFlux.zip(sources, (tuple: JTuple2[Any, Any]) => combinator(tuple.getT1, tuple.getT2)))
  def zip[V](sources: Publisher[_ <: Publisher[_]], combinator: (Any, Any, Any) => V): Flux[V] = wrap(JFlux.zip(sources, (tuple: JTuple3[Any, Any, Any]) => combinator(tuple.getT1, tuple.getT2, tuple.getT3)))
  def zip[V](sources: Publisher[_ <: Publisher[_]], combinator: (Any, Any, Any, Any) => V): Flux[V] = wrap(JFlux.zip(sources, (tuple: JTuple4[Any, Any, Any, Any]) => combinator(tuple.getT1, tuple.getT2, tuple.getT3, tuple.getT4)))
  def zip[V](sources: Publisher[_ <: Publisher[_]], combinator: (Any, Any, Any, Any, Any) => V): Flux[V] = wrap(JFlux.zip(sources, (tuple: JTuple5[Any, Any, Any, Any, Any]) => combinator(tuple.getT1, tuple.getT2, tuple.getT3, tuple.getT4, tuple.getT5)))
  def zip[V](sources: Publisher[_ <: Publisher[_]], combinator: (Any, Any, Any, Any, Any, Any) => V): Flux[V] = wrap(JFlux.zip(sources, (tuple: JTuple6[Any, Any, Any, Any, Any, Any]) => combinator(tuple.getT1, tuple.getT2, tuple.getT3, tuple.getT4, tuple.getT5, tuple.getT6)))
  def zip[V](sources: Publisher[_ <: Publisher[_]], combinator: (Any, Any, Any, Any, Any, Any, Any) => V): Flux[V] = wrap(JFlux.zip(sources, (tuple: JTuple7[Any, Any, Any, Any, Any, Any, Any]) => combinator(tuple.getT1, tuple.getT2, tuple.getT3, tuple.getT4, tuple.getT5, tuple.getT6, tuple.getT7)))
  def zip[V](sources: Publisher[_ <: Publisher[_]], combinator: (Any, Any, Any, Any, Any, Any, Any, Any) => V): Flux[V] = wrap(JFlux.zip(sources, (tuple: JTuple8[Any, Any, Any, Any, Any, Any, Any, Any]) => combinator(tuple.getT1, tuple.getT2, tuple.getT3, tuple.getT4, tuple.getT5, tuple.getT6, tuple.getT7, tuple.getT8)))

}

final class Flux[T] (private val publisher: Publisher[T]) extends Publisher[T] with ImplicitJavaInterop {

  private val delegate: JFlux[T] = JFlux.from(publisher)

  ///
  /// API METHODS
  ///

  def blockLast(timeout: Duration = Duration.Inf): Option[T] = timeout match {
    case _: Infinite => Option(delegate.blockLast())
    case finiteDuration: FiniteDuration => Option(delegate.blockLast(finiteDuration))
  }

  def doOnError(onError: Throwable => Unit): Flux[T] = wrap(delegate.doOnError(onError))

  def doOnNext(onNext: T => Unit): Flux[T] = wrap(delegate.doOnNext(onNext))

  def map[V](mapper: T => V): Flux[V] = wrap(delegate.map(mapper))

  def subscribe(): Disposable = delegate.subscribe()

  def subscribe(consumer: T => Unit): Disposable = delegate.subscribe(consumer)

  def subscribe(consumer: T => Unit,
                errorConsumer: Throwable => Unit): Disposable = delegate.subscribe(consumer, errorConsumer)

//  def subscribe(consumer: Option[T => Unit],
//                errorConsumer: Throwable => Unit): Disposable = delegate.subscribe(consumer.orNull[T => Unit], errorConsumer)

  def subscribe(consumer: T => Unit,
                errorConsumer: Throwable => Unit,
                completeConsumer: () => Unit): Disposable = delegate.subscribe(consumer, errorConsumer, completeConsumer)

//  def subscribe(consumer: Option[T => Unit],
//                errorConsumer: Throwable => Unit,
//                completeConsumer: () => Unit): Disposable = delegate.subscribe(consumer.orNull[T => Unit], errorConsumer, completeConsumer)

//  def subscribe(consumer: T => Unit,
//                errorConsumer: Option[Throwable => Unit],
//                completeConsumer: () => Unit): Disposable = delegate.subscribe(consumer, errorConsumer.orNull[Throwable => Unit], completeConsumer)

//  def subscribe(consumer: Option[T => Unit],
//                errorConsumer: Option[Throwable => Unit],
//                completeConsumer: () => Unit): Disposable = delegate.subscribe(consumer.orNull[T => Unit], errorConsumer.orNull[Throwable => Unit], completeConsumer)

//
//  def subscribe(consumer: Option[T => Unit] = None,
//                errorConsumer: Option[Throwable => Unit] = None,
//                completeConsumer: Option[Runnable] = None): Disposable = delegate.subscribe(
//      consumer.orNull[T => Unit],
//      errorConsumer.orNull[Throwable => Unit],
//      completeConsumer.orNull[Runnable])




  ///
  /// BRIDGE METHODS
  ///

  override def subscribe(s: Subscriber[_ >: T]): Unit = delegate.subscribe(s)

}