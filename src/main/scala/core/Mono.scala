package core

import java.util.concurrent.{Callable, CompletionStage}

import core.JavaInterop._
import org.reactivestreams.{Publisher, Subscriber}
import reactor.core.publisher.FluxSink.OverflowStrategy
import reactor.core.{Disposable}
import reactor.core.publisher.{FluxSink, MonoSink, SynchronousSink, Flux => JFlux, Mono => JMono}
import reactor.core.scheduler.Scheduler
import java.util.concurrent.{Future => JFuture}

import core.Flux.{toScalaTuple2, toScalaTuple3, toScalaTuple4, toScalaTuple5, toScalaTuple6, toScalaTuple7, toScalaTuple8}
import reactor.util.context.Context
import core.JavaInterop._

import scala.concurrent.Future
import scala.concurrent.duration.Duration.Infinite
import scala.concurrent.duration.{Duration, FiniteDuration}
import scala.language.{existentials, higherKinds}

object Mono extends ImplicitJavaInterop {

  ///
  /// API METHODS
  ///

  def create[T](callback: MonoSink[T] => Unit): Mono[T] = wrapMono(JMono.create(callback))
  def defer[T](supplier: () => Mono[T]): Mono[T] = wrapMono(JMono.defer(() => supplier.apply().delegate))
  def deferWithContext[T](supplier: Context => Mono[T]): Mono[T] = wrapMono(JMono.deferWithContext((c: Context) => supplier(c).delegate))

  def delay(duration: Duration): Mono[Long] = duration match {
    case _: Infinite => wrapMono(JMono.never())
    case finiteDuration: FiniteDuration => wrapMono(JMono.delay(finiteDuration).map(Long2long))
  }

  def empty[T](): Mono[T] = wrapMono(JMono.empty())

  def error[T](error: Throwable): Mono[T] = wrapMono(JMono.error(error))
  def error[T](errorSupplier: () => Throwable): Mono[T] = wrapMono(JMono.error(errorSupplier))

  // todo apply varargs pattern to other cases (especially needed in flux)
  def first[T](): Mono[T] = first(Seq())
  def first[T](mono: Mono[T], monos: Mono[T]*): Mono[T] = first(mono +: monos)
  def first[T](monos: Iterable[_ <: Mono[T]]): Mono[T] = wrapMono(JMono.first(monos.map(_.delegate)))

  def from[T](source: Publisher[T]): Mono[T] = wrapMono(JMono.from(source))
  def fromCallable[T](supplier: Callable[T]): Mono[T] = wrapMono(JMono.fromCallable(supplier))
//  def fromCompletionStage[T](completionStage: CompletionStage[T]): Mono[T] = wrap(JMono.fromCompletionStage(completionStage))
//  def fromCompletionStage[T](stageSupplier: () => CompletionStage[T]): Mono[T] = wrap(JMono.fromCompletionStage(stageSupplier))
  def fromDirect[I](source: Publisher[I]): Mono[I] = wrapMono(JMono.fromDirect(source))
  def fromFuture[T](future: Future[T]): Mono[T] = wrapMono(JMono.fromCompletionStage(future)) // completionStage is java equiv of Future
  def fromFutureSupplier[T](futureSupplier: () => Future[T]): Mono[T] = wrapMono(JMono.fromCompletionStage(() => futureSupplier.apply()))
  def fromRunnable[T](runnable: () => Unit): Mono[T] = wrapMono(JMono.fromRunnable(runnable))
  def fromSupplier[T](supplier: () => T): Mono[T] = wrapMono(JMono.fromSupplier(supplier))

  def ignoreElements[T](source: Publisher[T]): Mono[T] = wrapMono(JMono.ignoreElements(source))

  def just[T](data: T): Mono[T] = wrapMono(JMono.just(data))
  def justOrEmpty[T](data: Option[T]): Mono[T] = wrapMono(JMono.justOrEmpty(asJavaOptional(data)))

  def never[T](): Mono[T] = wrapMono(JMono.never())

  def sequenceEqual[T](source1: Publisher[T], source2: Publisher[T]): Mono[Boolean] = wrapMono(JMono.sequenceEqual(source1, source2).asInstanceOf[JMono[Boolean]])
  def sequenceEqual[T](source1: Publisher[T], source2: Publisher[T], isEqual: (T, T) => Boolean): Mono[Boolean] = wrapMono(JMono.sequenceEqual(source1, source2, isEqual).asInstanceOf[JMono[Boolean]])
  def sequenceEqual[T](source1: Publisher[T], source2: Publisher[T], isEqual: (T, T) => Boolean, prefetch: Int): Mono[Boolean] = wrapMono(JMono.sequenceEqual(source1, source2, isEqual, prefetch).asInstanceOf[JMono[Boolean]])

  def subscriberContext(): Mono[Context] = wrapMono(JMono.subscriberContext())

  // todo: look into scala Callable equivalent for using in both Mono and Flux apis
  def using[T, D](resourceSupplier: Callable[D], sourceSupplier: D => Mono[T], resourceCleanup: D => Unit): Mono[T] = wrapMono(JMono.using(resourceSupplier, sourceSupplier.andThen(_.delegate), resourceCleanup))
  def using[T, D](resourceSupplier: Callable[D], sourceSupplier: D => Mono[T], resourceCleanup: D => Unit, eager: Boolean): Mono[T] = wrapMono(JMono.using(resourceSupplier, sourceSupplier.andThen(_.delegate), resourceCleanup, eager))

  def usingWhen[T, D](resourceSupplier: Publisher[D], resourceClosure: D => Mono[T], asyncComplete: D => Publisher[_], asyncError: D => Publisher[_]): Mono[T] = wrapMono(JMono.usingWhen(resourceSupplier, resourceClosure.andThen(_.delegate), asyncComplete, asyncError))
  def usingWhen[T, D](resourceSupplier: Publisher[D], resourceClosure: D => Mono[T], asyncComplete: D => Publisher[_], asyncError: D => Publisher[_], asyncCancel: D => Publisher[_]): Mono[T] = wrapMono(JMono.usingWhen(resourceSupplier, resourceClosure.andThen(_.delegate), asyncComplete, asyncError, asyncCancel))

  def when(): Mono[_] = when(Seq())
  def when(source: Publisher[Any], sources: Publisher[Any]*): Mono[_] = when(source +: sources)
  def when(sources: Iterable[_ <: Publisher[Any]]): Mono[_] = wrapMono(JMono.when(sources))


  def whenDelayError(): Mono[_] = whenDelayError(Seq())
  def whenDelayError(source: Publisher[Any], sources: Publisher[Any]*): Mono[_] = whenDelayError(source +: sources)
  def whenDelayError(sources: Iterable[_ <: Publisher[Any]]): Mono[_] = wrapMono(JMono.whenDelayError(sources))


  def zip[T1, T2, O](p1: Mono[T1], p2: Mono[T2], combinator: (T1, T2) => O): Mono[O] = wrapMono(JMono.zip(p1.delegate, p2.delegate, combinator))

  def zip[T1, T2](p1: Mono[T1], p2: Mono[T2]): Mono[(T1, T2)] = wrapMono(JMono.zip(p1.delegate, p2.delegate).map(toScalaTuple2(_)))
  def zip[T1, T2, T3](p1: Mono[T1], p2: Mono[T2], p3: Mono[T3]): Mono[(T1, T2, T3)] = wrapMono(JMono.zip(p1.delegate, p2.delegate, p3.delegate).map(toScalaTuple3(_)))
  def zip[T1, T2, T3, T4](p1: Mono[T1], p2: Mono[T2], p3: Mono[T3], p4: Mono[T4]): Mono[(T1, T2, T3, T4)] = wrapMono(JMono.zip(p1.delegate, p2.delegate, p3.delegate, p4.delegate).map(toScalaTuple4(_)))
  def zip[T1, T2, T3, T4, T5](p1: Mono[T1], p2: Mono[T2], p3: Mono[T3], p4: Mono[T4], p5: Mono[T5]): Mono[(T1, T2, T3, T4, T5)] = wrapMono(JMono.zip(p1.delegate, p2.delegate, p3.delegate, p4.delegate, p5.delegate).map(toScalaTuple5(_)))
  def zip[T1, T2, T3, T4, T5, T6](p1: Mono[T1], p2: Mono[T2], p3: Mono[T3], p4: Mono[T4], p5: Mono[T5], p6: Mono[T6]): Mono[(T1, T2, T3, T4, T5, T6)] = wrapMono(JMono.zip(p1.delegate, p2.delegate, p3.delegate, p4.delegate, p5.delegate, p6.delegate).map(toScalaTuple6(_)))
  def zip[T1, T2, T3, T4, T5, T6, T7](p1: Mono[T1], p2: Mono[T2], p3: Mono[T3], p4: Mono[T4], p5: Mono[T5], p6: Mono[T6], p7: Mono[T7]): Mono[(T1, T2, T3, T4, T5, T6, T7)] = wrapMono(JMono.zip(p1.delegate, p2.delegate, p3.delegate, p4.delegate, p5.delegate, p6.delegate, p7.delegate).map(toScalaTuple7(_)))
  def zip[T1, T2, T3, T4, T5, T6, T7, T8](p1: Mono[T1], p2: Mono[T2], p3: Mono[T3], p4: Mono[T4], p5: Mono[T5], p6: Mono[T6], p7: Mono[T7], p8: Mono[T8]): Mono[(T1, T2, T3, T4, T5, T6, T7, T8)] = wrapMono(JMono.zip(p1.delegate, p2.delegate, p3.delegate, p4.delegate, p5.delegate, p6.delegate, p7.delegate, p8.delegate).map(toScalaTuple8(_)))

  def zip[R](monos: Iterable[Mono[_]], combinator: (_ >: Array[AnyRef]) => R): Mono[R] = wrapMono(JMono.zip(monos.map(_.delegate), combinator))
  def zip[R](combinator: (_ >: Array[AnyRef]) => R, monos: Mono[_]*): Mono[R] = wrapMono(JMono.zip(combinator, monos.map(_.delegate):_*))

  def zipDelayError[T1, T2](p1: Mono[T1], p2: Mono[T2]): Mono[(T1, T2)] = wrapMono(JMono.zipDelayError(p1.delegate, p2.delegate).map(toScalaTuple2(_)))
  def zipDelayError[T1, T2, T3](p1: Mono[T1], p2: Mono[T2], p3: Mono[T3]): Mono[(T1, T2, T3)] = wrapMono(JMono.zipDelayError(p1.delegate, p2.delegate, p3.delegate).map(toScalaTuple3(_)))
  def zipDelayError[T1, T2, T3, T4](p1: Mono[T1], p2: Mono[T2], p3: Mono[T3], p4: Mono[T4]): Mono[(T1, T2, T3, T4)] = wrapMono(JMono.zipDelayError(p1.delegate, p2.delegate, p3.delegate, p4.delegate).map(toScalaTuple4(_)))
  def zipDelayError[T1, T2, T3, T4, T5](p1: Mono[T1], p2: Mono[T2], p3: Mono[T3], p4: Mono[T4], p5: Mono[T5]): Mono[(T1, T2, T3, T4, T5)] = wrapMono(JMono.zipDelayError(p1.delegate, p2.delegate, p3.delegate, p4.delegate, p5.delegate).map(toScalaTuple5(_)))
  def zipDelayError[T1, T2, T3, T4, T5, T6](p1: Mono[T1], p2: Mono[T2], p3: Mono[T3], p4: Mono[T4], p5: Mono[T5], p6: Mono[T6]): Mono[(T1, T2, T3, T4, T5, T6)] = wrapMono(JMono.zip(p1.delegate, p2.delegate, p3.delegate, p4.delegate, p5.delegate, p6.delegate).map(toScalaTuple6(_)))
  def zipDelayError[T1, T2, T3, T4, T5, T6, T7](p1: Mono[T1], p2: Mono[T2], p3: Mono[T3], p4: Mono[T4], p5: Mono[T5], p6: Mono[T6], p7: Mono[T7]): Mono[(T1, T2, T3, T4, T5, T6, T7)] = wrapMono(JMono.zipDelayError(p1.delegate, p2.delegate, p3.delegate, p4.delegate, p5.delegate, p6.delegate, p7.delegate).map(toScalaTuple7(_)))
  def zipDelayError[T1, T2, T3, T4, T5, T6, T7, T8](p1: Mono[T1], p2: Mono[T2], p3: Mono[T3], p4: Mono[T4], p5: Mono[T5], p6: Mono[T6], p7: Mono[T7], p8: Mono[T8]): Mono[(T1, T2, T3, T4, T5, T6, T7, T8)] = wrapMono(JMono.zipDelayError(p1.delegate, p2.delegate, p3.delegate, p4.delegate, p5.delegate, p6.delegate, p7.delegate, p8.delegate).map(toScalaTuple8(_)))

  def zipDelayError[R](monos: Iterable[Mono[_]], combinator: (_ >: Array[AnyRef]) => R): Mono[R] = wrapMono(JMono.zipDelayError(monos.map(_.delegate), combinator))
  def zipDelayError[R](combinator: (_ >: Array[AnyRef]) => R, monos: Mono[_]*): Mono[R] = wrapMono(JMono.zipDelayError(combinator, monos.map(_.delegate):_*))

}

final class Mono[T] (private val publisher: Publisher[T]) extends Publisher[T] with ImplicitJavaInterop {

  private[core] val delegate: JMono[T] = JMono.from(publisher)

  ///
  /// API METHODS
  ///

  def as[P](transformer: Mono[T] => P): P = delegate.as((jm: JMono[T]) => transformer.apply(wrapMono(jm)))

  def and(other: Publisher[_]): Mono[_] = wrapMono(delegate.and(other))

  def block(): T = delegate.block()

  def block(timeout: Duration): T = timeout match {
    case _: Infinite => delegate.block()
    case finiteDuration: FiniteDuration => delegate.block(finiteDuration)
  }

  def blockOptional(): Option[T] = Option(delegate.block())

  def blockOptional(timeout: Duration): Option[T] = timeout match {
    case _: Infinite => Option(delegate.block())
    case finiteDuration: FiniteDuration => Option(delegate.block(finiteDuration))
  }

  def doOnError(onError: Throwable => Unit): Mono[T] = wrapMono(delegate.doOnError(onError))

  def doOnNext(onNext: T => Unit): Mono[T] = wrapMono(delegate.doOnNext(onNext))

  def map[V](mapper: T => V): Mono[V] = wrapMono(delegate.map(mapper))

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