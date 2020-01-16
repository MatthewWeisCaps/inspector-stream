package core

import java.util
import java.util.{Comparator, function}
import java.util.function.{BiConsumer, BiFunction, Consumer, LongConsumer, Predicate, Supplier}
import java.util.logging.Level

import core.JavaInterop._
import org.reactivestreams.{Publisher, Subscriber, Subscription}
import reactor.core.{CoreSubscriber, Disposable, publisher}
import reactor.core.publisher.{Signal, SignalType, Flux => JFlux, GroupedFlux => JGroupedFlux, ParallelFlux => JParallelFlux}
import reactor.core.scheduler.{Scheduler, Schedulers}
import reactor.util.context.Context

final class ParallelFlux[T](protected val delegate: JParallelFlux[T]) extends Publisher[T] {

//  override def subscribe(s: Subscriber[_ >: T]): Unit = delegate.subscribe(s)

  def as[U](converter: (_ >: ParallelFlux[T]) => U): U = delegate.as(asJavaFn1((jpf: JParallelFlux[T]) => converter(wrapParallelFlux(jpf))))

  def checkpoint(): ParallelFlux[T] = wrapParallelFlux(delegate.checkpoint())
  def checkpoint(description: String): ParallelFlux[T] = wrapParallelFlux(delegate.checkpoint(description))
  def checkpoint(description: String, forceStackTrace: Boolean): ParallelFlux[T] = wrapParallelFlux(delegate.checkpoint(description, forceStackTrace))

//  def collect[C](collectionSupplier: Supplier[_ <: C], collector: BiConsumer[_ >: C, _ >: T]): ParallelFlux[C] = wrapParallelFlux(delegate.collect(collectionSupplier, collector))
  def collect[C](collectionSupplier: () => C, collector: (C, T) => Unit): ParallelFlux[C] = wrapParallelFlux(delegate.collect(asJavaSupplier(collectionSupplier), asJavaBiConsumer(collector)))

//  def collectSortedList(comparator: Comparator[_ >: T]): Mono[List[T]] = wrapMono(delegate.collectSortedList(comparator).map(asScalaList))
//  def collectSortedList(comparator: Comparator[_ >: T], capacityHint: Int): Mono[List[T]] = wrapMono(delegate.collectSortedList(comparator, capacityHint).map(asScalaList))

  def collectSortedList(ordering: Ordering[T]): Mono[Seq[T]] = wrapMono(delegate.collectSortedList(ordering).map(toScalaSeq(_)))
  def collectSortedList(ordering: Ordering[T], capacityHint: Int): Mono[Seq[T]] = wrapMono(delegate.collectSortedList(ordering, capacityHint).map(toScalaSeq(_)))

//  def composeGroup[U](composer: function.Function[_ >: GroupedFlux[Integer, T], _ <: Publisher[_ <: U]]): ParallelFlux[U] = delegate.composeGroup(composer)

//  def concatMap[R](mapper: function.Function[_ >: T, _ <: Publisher[_ <: R]]): ParallelFlux[R] = wrapParallelFlux(delegate.concatMap(mapper))
//  def concatMap[R](mapper: function.Function[_ >: T, _ <: Publisher[_ <: R]], prefetch: Int): ParallelFlux[R] = wrapParallelFlux(delegate.concatMap(mapper, prefetch))

//  def concatMap[V](mapper: T => Publisher[V]): Flux[V] = wrapFlux[V](delegate.concatMap(asJavaFn1(mapper)))
//  def concatMap[V](mapper: T => Publisher[V], prefetch: Int): Flux[V] = wrapFlux[V](delegate.concatMap(asJavaFn1(mapper), prefetch))

  def concatMap[V](mapper: T => Publisher[V]): ParallelFlux[V] = wrapParallelFlux[V](delegate.concatMap(asJavaFn1(mapper)))
  def concatMap[V](mapper: T => Publisher[V], prefetch: Int): ParallelFlux[V] = wrapParallelFlux[V](delegate.concatMap(asJavaFn1(mapper), prefetch))

//  def concatMapDelayError[R](mapper: function.Function[_ >: T, _ <: Publisher[_ <: R]]): ParallelFlux[R] = wrapParallelFlux(delegate.concatMapDelayError(mapper))

  def concatMapDelayError[V](mapper: T => Publisher[V]): ParallelFlux[V] = wrapParallelFlux[V](delegate.concatMapDelayError(asJavaFn1(mapper)))


//  def doAfterTerminate(afterTerminate: Runnable): ParallelFlux[T] = wrapParallelFlux(delegate.doAfterTerminate(afterTerminate))
  def doAfterTerminate(afterTerminate: () => Unit): ParallelFlux[T] = wrapParallelFlux[T](delegate.doAfterTerminate(asJavaRunnable(afterTerminate)))


//  def doOnCancel(onCancel: Runnable): ParallelFlux[T] = wrapParallelFlux(delegate.doOnCancel(onCancel))
//  def doOnComplete(onComplete: Runnable): ParallelFlux[T] = wrapParallelFlux(delegate.doOnComplete(onComplete))
//  def doOnEach(signalConsumer: Consumer[_ >: Signal[T]]): ParallelFlux[T] = wrapParallelFlux(delegate.doOnEach(signalConsumer))
//  def doOnError(onError: Consumer[_ >: Throwable]): ParallelFlux[T] = wrapParallelFlux(delegate.doOnError(onError))
//  def doOnSubscribe(onSubscribe: Consumer[_ >: Subscription]): ParallelFlux[T] = wrapParallelFlux(delegate.doOnSubscribe(onSubscribe))
//  def doOnNext(onNext: Consumer[_ >: T]): ParallelFlux[T] = wrapParallelFlux(delegate.doOnNext(onNext))
//  def doOnRequest(onRequest: LongConsumer): ParallelFlux[T] = wrapParallelFlux(delegate.doOnRequest(onRequest))
//  def doOnTerminate(onTerminate: Runnable): ParallelFlux[T] = wrapParallelFlux(delegate.doOnTerminate(onTerminate))

  def doOnCancel(onCancel: () => Unit): ParallelFlux[T] = wrapParallelFlux[T](delegate.doOnCancel(asJavaRunnable(onCancel)))
  def doOnComplete(onComplete: () => Unit): ParallelFlux[T] = wrapParallelFlux[T](delegate.doOnComplete(asJavaRunnable(onComplete)))
  def doOnEach(signalConsumer: Signal[T] => Unit): ParallelFlux[T] = wrapParallelFlux[T](delegate.doOnEach(asJavaConsumer(signalConsumer)))
  def doOnError(onError: Throwable => Unit): ParallelFlux[T] = wrapParallelFlux[T](delegate.doOnError(asJavaConsumer(onError)))
  def doOnSubscribe(onSubscribe: Subscription => Unit): ParallelFlux[T] = wrapParallelFlux[T](delegate.doOnSubscribe(asJavaConsumer(onSubscribe)))
  def doOnNext(onNext: T => Unit): ParallelFlux[T] = wrapParallelFlux[T](delegate.doOnNext(asJavaConsumer(onNext)))
  def doOnRequest(consumer: Long => Unit): ParallelFlux[T] = wrapParallelFlux[T](delegate.doOnRequest(asJavaLongConsumer(consumer)))
  def doOnTerminate(onTerminate: () => Unit): ParallelFlux[T] = wrapParallelFlux[T](delegate.doOnTerminate(asJavaRunnable(onTerminate)))

//  def filter(predicate: Predicate[_ >: T]): ParallelFlux[T] = delegate.filter(predicate)
  def filter(predicate: T => Boolean): ParallelFlux[T] = wrapParallelFlux[T](delegate.filter(asJavaPredicate(predicate)))


//  def flatMap[R](mapper: function.Function[_ >: T, _ <: Publisher[_ <: R]]): ParallelFlux[R] = delegate.flatMap(mapper)
//  def flatMap[R](mapper: function.Function[_ >: T, _ <: Publisher[_ <: R]], delayError: Boolean): ParallelFlux[R] = delegate.flatMap(mapper, delayError)
//  def flatMap[R](mapper: function.Function[_ >: T, _ <: Publisher[_ <: R]], delayError: Boolean, maxConcurrency: Int): ParallelFlux[R] = delegate.flatMap(mapper, delayError, maxConcurrency)
//  def flatMap[R](mapper: function.Function[_ >: T, _ <: Publisher[_ <: R]], delayError: Boolean, maxConcurrency: Int, prefetch: Int): ParallelFlux[R] = delegate.flatMap(mapper, delayError, maxConcurrency, prefetch)

  def flatMap[R](mapper: T => Publisher[R]): ParallelFlux[R] = wrapParallelFlux[R](delegate.flatMap(asJavaFn1(mapper)))
  def flatMap[R](mapper: T => Publisher[R], delayError: Boolean): ParallelFlux[R] = wrapParallelFlux[R](delegate.flatMap(asJavaFn1(mapper), delayError))
  def flatMap[R](mapper: T => Publisher[R], delayError: Boolean, concurrency: Int): ParallelFlux[R] = wrapParallelFlux[R](delegate.flatMap(asJavaFn1(mapper), delayError, concurrency))
  def flatMap[R](mapper: T => Publisher[R], delayError: Boolean, concurrency: Int, prefetch: Int): ParallelFlux[R] = wrapParallelFlux[R](delegate.flatMap(asJavaFn1(mapper), delayError, concurrency, prefetch))

//  def groups(): publisher.Flux[GroupedFlux[Integer, T]] = delegate.groups()

  // todo
//  def groups(): Flux[GroupedFlux[Int, T]] = wrapFlux(delegate.groups().map(gf => wrapGroupedFlux(gf)).groupBy((key: Integer) => Integer2int(key)))
//  def groups(): Flux[GroupedFlux[Int, T]] = wrapFlux(delegate.groups().map(wrapGroupedFlux).groupBy(Integer2int))
  def groups(): Flux[GroupedFlux[Int, T]] = {
    // types needed to be carefully defined to avoid type error when wrapping and mapping Integer to Int
    val wrapper: JGroupedFlux[Integer, T] => GroupedFlux[Integer, T] = gf => wrapGroupedFlux[Integer, T](gf)
    val caster: GroupedFlux[Integer, T] => GroupedFlux[Int, T] = gf => gf.asInstanceOf[GroupedFlux[Int, T]]
    wrapFlux(delegate.groups()).map(wrapper).map(caster)
  }

//  def hide(): ParallelFlux[T] = delegate.hide()
  def hide(): ParallelFlux[T] = wrapParallelFlux(delegate.hide())

  def log(): ParallelFlux[T] = wrapParallelFlux(delegate.log())
  def log(category: String): ParallelFlux[T] = wrapParallelFlux(delegate.log(category))
  def log(category: String, level: Level, options: SignalType*): ParallelFlux[T] = wrapParallelFlux(delegate.log(category, level, options:_*))
  def log(category: String, level: Level, showOperatorLine: Boolean, options: SignalType*): ParallelFlux[T] = wrapParallelFlux(delegate.log(category, level, showOperatorLine, options:_*))

//  def map[U](mapper: function.Function[_ >: T, _ <: U]): ParallelFlux[U] = delegate.map(mapper)
  def map[U](mapper: T => U): ParallelFlux[U] = wrapParallelFlux[U](delegate.map(asJavaFn1(mapper)))

  def name(name: String): ParallelFlux[T] = wrapParallelFlux(delegate.name(name))

//  def ordered(comparator: Comparator[_ >: T]): publisher.Flux[T] = delegate.ordered(comparator)
//  def ordered(comparator: Comparator[_ >: T], prefetch: Int): publisher.Flux[T] = delegate.ordered(comparator, prefetch)
  def ordered(ordering: Ordering[_ >: T]): Flux[T] = wrapFlux[T](delegate.ordered(ordering))
  def ordered(ordering: Ordering[_ >: T], prefetch: Int): Flux[T] = wrapFlux[T](delegate.ordered(ordering, prefetch))

  def parallelism(): Int = delegate.parallelism()

//  def reduce(reducer: BiFunction[T, T, T]): publisher.Mono[T] = delegate.reduce(reducer)
//  def reduce[R](initialSupplier: Supplier[R], reducer: BiFunction[R, _ >: T, R]): ParallelFlux[R] = delegate.reduce(initialSupplier, reducer)

  def reduce(reducer: (T, T) => T): Mono[T] = wrapMono[T](delegate.reduce(asJavaFn2(reducer)))
  def reduce[R](initialSupplier: () => R, reducer: (R, T) => R): ParallelFlux[R] = wrapParallelFlux[R](delegate.reduce[R](asJavaSupplier(initialSupplier), asJavaFn2(reducer)))

  def runOn(scheduler: Scheduler): ParallelFlux[T] = wrapParallelFlux(delegate.runOn(scheduler))
  def runOn(scheduler: Scheduler, prefetch: Int): ParallelFlux[T] = wrapParallelFlux(delegate.runOn(scheduler, prefetch))

  def sequential(): publisher.Flux[T] = delegate.sequential()
  def sequential(prefetch: Int): publisher.Flux[T] = delegate.sequential(prefetch)

//  def sorted(comparator: Comparator[_ >: T]): publisher.Flux[T] = delegate.sorted(comparator)
//  def sorted(comparator: Comparator[_ >: T], capacityHint: Int): publisher.Flux[T] = delegate.sorted(comparator, capacityHint)

  def sorted(ordering: Ordering[_ >: T]): Flux[T] = wrapFlux[T](delegate.sorted(ordering))
  def sorted(ordering: Ordering[_ >: T], capacityHint: Int): Flux[T] = wrapFlux[T](delegate.sorted(ordering, capacityHint))

  def subscribe(): Disposable = delegate.subscribe()
  def subscribe(onNext: (_ >: T) => Unit): Disposable = delegate.subscribe(asJavaConsumer(onNext))
  def subscribe(onNext: (_ >: T) => Unit, onError: (_ >: Throwable) => Unit): Disposable = delegate.subscribe(asJavaConsumer(onNext), asJavaConsumer(onError))
  def subscribe(onNext: (_ >: T) => Unit, onError: (_ >: Throwable) => Unit, onComplete: () => Unit): Disposable = delegate.subscribe(asJavaConsumer(onNext), asJavaConsumer(onError), asJavaRunnable(onComplete))
//  def subscribe(s: CoreSubscriber[_ >: T]): Unit = delegate.subscribe(s)
  def subscribe(onNext: (_ >: T) => Unit, onError: (_ >: Throwable) => Unit, onComplete: () => Unit, onSubscribe: (_ >: Subscription) => Unit): Disposable = {

    delegate.subscribe(asJavaConsumer(onNext), asJavaConsumer(onError), asJavaRunnable(onComplete), asJavaConsumer(onSubscribe))
  }
  // depreciated:
//  def subscribe(onNext: (_ >: T) => Unit, onError: (_ >: Throwable) => Unit, onComplete: () => Unit, initialContext: Context): Disposable = delegate.subscribe(asJavaConsumer(onNext), asJavaConsumer(onError), asJavaRunnable(onComplete), initialContext)
  override def subscribe(s: Subscriber[_ >: T]): Unit = delegate.subscribe(s)

//  def subscribe(): Disposable = delegate.subscribe()
//  def subscribe(consumer: T => Unit): Disposable = delegate.subscribe(asJavaConsumer(consumer))
//  def subscribe(consumer: T => Unit, errorConsumer: Throwable => Unit): Disposable = delegate.subscribe(asJavaConsumer(consumer), asJavaConsumer(errorConsumer))
//  def subscribe(consumer: T => Unit, errorConsumer: Throwable => Unit, completeConsumer: () => Unit): Disposable = delegate.subscribe(asJavaConsumer(consumer), asJavaConsumer(errorConsumer), asJavaRunnable(completeConsumer))
//  def subscribe(consumer: T => Unit, errorConsumer: Throwable => Unit, completeConsumer: () => Unit, subscriptionConsumer: Subscription => Unit): Disposable = delegate.subscribe(asJavaConsumer(consumer), asJavaConsumer(errorConsumer), asJavaRunnable(completeConsumer), asJavaConsumer(subscriptionConsumer))
//  def subscribe(consumer: T => Unit, errorConsumer: Throwable => Unit, completeConsumer: () => Unit, initialContext: Context): Disposable = delegate.subscribe(asJavaConsumer(consumer), asJavaConsumer(errorConsumer), asJavaRunnable(completeConsumer), initialContext)

  def tag(key: String, value: String): ParallelFlux[T] = wrapParallelFlux(delegate.tag(key, value))

//  def `then`(): publisher.Mono[Void] = delegate.`then`()
  def `then`(): Mono[Void] = wrapMono(delegate.`then`())

//  def transform[U](composer: function.Function[_ >: ParallelFlux[T], ParallelFlux[U]]): ParallelFlux[U] = delegate.transform(composer)
//  def transformGroups[U](composer: function.Function[_ >: GroupedFlux[Integer, T], _ <: Publisher[_ <: U]]): ParallelFlux[U] = delegate.transformGroups(composer)

  // todo test transform, transformGroups, group()
  def transform[U](composer: (_ >: ParallelFlux[T]) => ParallelFlux[U]): ParallelFlux[U] = {
    // instead of ParallelFlux => ParallelFlux, create a composer for JParallelFlux => JParallelFLux
    val jcomposer: (_ >: JParallelFlux[T]) => JParallelFlux[U] =
      (jgf: JParallelFlux[T]) => composer.apply(wrapParallelFlux(jgf)).delegate

    wrapParallelFlux[U](delegate.transform[U](asJavaFn1(jcomposer)))
  }

  def transformGroups[U](composer: (_ >: GroupedFlux[Int, T]) => (_ <: Publisher[_ <: U])): ParallelFlux[U] = {
    val casted: (_ >: GroupedFlux[Integer, T]) => (_ <: Publisher[_ <: U]) =
      composer.asInstanceOf[(_ >: GroupedFlux[Integer, T]) => (_ <: Publisher[_ <: U])]

    val wrapper: (_ >: JGroupedFlux[Integer, T]) => (_ <: Publisher[_ <: U]) =
      (jgf: JGroupedFlux[Integer, T]) => casted.apply(wrapGroupedFlux(jgf))

    val fn: function.Function[_ >: JGroupedFlux[Integer, T], Publisher[_ <: U]] = asJavaFn1(wrapper)

    wrapParallelFlux[U](delegate.transformGroups[U](fn))
  }

  override def toString: String = delegate.toString

  def getPrefetch: Int = delegate.getPrefetch
}

object ParallelFlux {

  def from[T](source: Publisher[_ <: T]): ParallelFlux[T] = wrapParallelFlux(VarargsHelper.parallelFlux_fromSingleHelper(source))
  def from[T](source: Publisher[_ <: T], parallelism: Int): ParallelFlux[T] = wrapParallelFlux(JParallelFlux.from(source, parallelism))
  def from[T](source: Publisher[_ <: T], parallelism: Int, prefetch: Int, queueSupplier: () => util.Queue[T]): ParallelFlux[T] = wrapParallelFlux(JParallelFlux.from(source, parallelism, prefetch, asJavaSupplier(queueSupplier)))
  def from[T](publishers: Publisher[T]*): ParallelFlux[T] = wrapParallelFlux(JParallelFlux.from(publishers:_*))

}