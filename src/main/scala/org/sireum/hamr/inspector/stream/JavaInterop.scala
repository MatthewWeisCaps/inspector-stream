package org.sireum.hamr.inspector.stream

import java.lang.{Boolean => JBoolean, Iterable => JIterable, Long => JLong, Runnable => JRunnable}
import java.time.{Duration => JDuration}
import java.util
import java.util.{Iterator => JIterator}
import java.util.concurrent.{CompletionStage, Callable => JCallable}
import java.util.function.{BiConsumer => JBiConsumer, BiFunction => JBiFunction, BiPredicate => JBiPredicate, BooleanSupplier => JBooleanSupplier, Consumer => JConsumer, Function => JFunction, LongConsumer => JLongConsumer, Predicate => JPredicate, Supplier => JSupplier}
import java.util.stream.StreamSupport
import java.util.{List => JList, Map => JMap, Optional => JOptional}

import reactor.core.publisher.{ConnectableFlux => JConnectableFlux, Flux => JFlux, FluxSink => JFluxSink, GroupedFlux => JGroupedFlux, Mono => JMono, ParallelFlux => JParallelFlux}
import reactor.util.function.{Tuples, Tuple2 => JTuple2, Tuple3 => JTuple3, Tuple4 => JTuple4, Tuple5 => JTuple5, Tuple6 => JTuple6, Tuple7 => JTuple7, Tuple8 => JTuple8}

import scala.collection.{JavaConverters, mutable}
import scala.collection.JavaConverters._
import scala.compat.java8.StreamConverters
import scala.concurrent.Future
import scala.concurrent.duration.{Duration, FiniteDuration}

object JavaInterop {

  def wrapFlux[T](jFlux: JFlux[T]): Flux[T] = new FluxImpl[T](jFlux)
//  def wrapFlux[T](jFlux: JFlux[T]): Flux[T] = Flux.from(jFlux) // todo switch to Flux.from except for Object methods? (benchmark)

  def wrapParallelFlux[T](jParallelFlux: JParallelFlux[T]): ParallelFlux[T] = new ParallelFlux[T](jParallelFlux)
  def wrapConnectableFlux[T](jConnectableFlux: JConnectableFlux[T]): ConnectableFlux[T] = new ConnectableFlux[T](jConnectableFlux)
  def wrapGroupedFlux[K, V](jGroupedFlux: JGroupedFlux[K, V]): GroupedFlux[K, V] = new GroupedFlux[K, V](jGroupedFlux)

  def wrapMono[T](jMono: JMono[T]): Mono[T] = new MonoImpl[T](jMono)
//  def wrapMono[T](jMono: JMono[T]): Mono[T] = Mono.from(jMono) // todo switch to Mono.from except for Object methods? (benchmark)

  def toJavaStream[T](stream: Stream[T]): java.util.stream.Stream[T] = StreamSupport.stream(stream.toIterable.asJava.spliterator(), false)

  def toScalaSeq[T](collection: java.util.Collection[T]): Seq[T] = JavaConverters.asScalaIteratorConverter(collection.iterator()).asScala.toSeq
//  def toScalaSeq[T](list: java.util.Iterator[T]): Seq[T] = JavaConverters.asScalaIteratorConverter(list).asScala.toSeq
  def toScalaMap[K, V](map: java.util.Map[K, V]): Map[K, V] = JavaConverters.mapAsScalaMapConverter(map).asScala.toMap

  def toJavaList[T](buffer: mutable.Buffer[T]): util.List[T] = JavaConverters.bufferAsJavaList(buffer)
  def toJavaMap[K, V](map: Map[K, V]): JMap[K, V] = JavaConverters.mapAsJavaMap(map)
  def toJavaMutableMap[K, V](map: mutable.Map[K, V]): JMap[K, V] = JavaConverters.mutableMapAsJavaMap(map)

  def asJavaLongConsumer(consumer: Long => Unit): JLongConsumer = (n: Long) => consumer.apply(long2Long(n))

  def toScalaFuture[T](completableFuture: CompletionStage[T]): Future[T] = scala.compat.java8.FutureConverters.toScala(completableFuture)
//  def toScalaFuture[T](completableFuture: JCompletableFuture[T]): Future[T] = scala.compat.java8.FutureConverters.toScala(completableFuture)

  def toScalaIterable[T](collection: util.Collection[T]): Iterable[T] = JavaConverters.collectionAsScalaIterable(collection)
  def toScalaIterable[T](iterable: JIterable[T]): Iterable[T] = JavaConverters.iterableAsScalaIterable(iterable)
  def toJavaCollection[T](iterable: mutable.Iterable[T]): util.Collection[T] = JavaConverters.asJavaCollection(iterable)
  def toJavaCollection[T](iterable: Iterable[T]): util.Collection[T] = JavaConverters.asJavaCollection(iterable)

  // tuples
  def toReactorTuple2[T1, T2](tuple: (T1, T2)): reactor.util.function.Tuple2[T1, T2] = Tuples.of(tuple._1, tuple._2)
  def toReactorTuple3[T1, T2, T3](tuple: (T1, T2, T3)): reactor.util.function.Tuple3[T1, T2, T3] = Tuples.of(tuple._1, tuple._2, tuple._3)
  def toReactorTuple4[T1, T2, T3, T4](tuple: (T1, T2, T3, T4)): reactor.util.function.Tuple4[T1, T2, T3, T4] = Tuples.of(tuple._1, tuple._2, tuple._3, tuple._4)
  def toReactorTuple5[T1, T2, T3, T4, T5](tuple: (T1, T2, T3, T4, T5)): reactor.util.function.Tuple5[T1, T2, T3, T4, T5] = Tuples.of(tuple._1, tuple._2, tuple._3, tuple._4, tuple._5)
  def toReactorTuple6[T1, T2, T3, T4, T5, T6](tuple: (T1, T2, T3, T4, T5, T6)): reactor.util.function.Tuple6[T1, T2, T3, T4, T5, T6] = Tuples.of(tuple._1, tuple._2, tuple._3, tuple._4, tuple._5, tuple._6)
  def toReactorTuple7[T1, T2, T3, T4, T5, T6, T7](tuple: (T1, T2, T3, T4, T5, T6, T7)): reactor.util.function.Tuple7[T1, T2, T3, T4, T5, T6, T7] = Tuples.of(tuple._1, tuple._2, tuple._3, tuple._4, tuple._5, tuple._6, tuple._7)
  def toReactorTuple8[T1, T2, T3, T4, T5, T6, T7, T8](tuple: (T1, T2, T3, T4, T5, T6, T7, T8)): reactor.util.function.Tuple8[T1, T2, T3, T4, T5, T6, T7, T8] = Tuples.of(tuple._1, tuple._2, tuple._3, tuple._4, tuple._5, tuple._6, tuple._7, tuple._8)

  def toScalaTuple2[T1, T2](tuple2: JTuple2[T1, T2]): (T1, T2) = (tuple2.getT1, tuple2.getT2)
  def toScalaTuple3[T1, T2, T3](tuple3: JTuple3[T1, T2, T3]): (T1, T2, T3) = (tuple3.getT1, tuple3.getT2, tuple3.getT3)
  def toScalaTuple4[T1, T2, T3, T4](tuple4: JTuple4[T1, T2, T3, T4]): (T1, T2, T3, T4) = (tuple4.getT1, tuple4.getT2, tuple4.getT3, tuple4.getT4)
  def toScalaTuple5[T1, T2, T3, T4, T5](tuple5: JTuple5[T1, T2, T3, T4, T5]): (T1, T2, T3, T4, T5) = (tuple5.getT1, tuple5.getT2, tuple5.getT3, tuple5.getT4, tuple5.getT5)
  def toScalaTuple6[T1, T2, T3, T4, T5, T6](tuple6: JTuple6[T1, T2, T3, T4, T5, T6]): (T1, T2, T3, T4, T5, T6) = (tuple6.getT1, tuple6.getT2, tuple6.getT3, tuple6.getT4, tuple6.getT5, tuple6.getT6)
  def toScalaTuple7[T1, T2, T3, T4, T5, T6, T7](tuple7: JTuple7[T1, T2, T3, T4, T5, T6, T7]): (T1, T2, T3, T4, T5, T6, T7) = (tuple7.getT1, tuple7.getT2, tuple7.getT3, tuple7.getT4, tuple7.getT5, tuple7.getT6, tuple7.getT7)
  def toScalaTuple8[T1, T2, T3, T4, T5, T6, T7, T8](tuple8: JTuple8[T1, T2, T3, T4, T5, T6, T7, T8]): (T1, T2, T3, T4, T5, T6, T7, T8) = (tuple8.getT1, tuple8.getT2, tuple8.getT3, tuple8.getT4, tuple8.getT5, tuple8.getT6, tuple8.getT7, tuple8.getT8)

  ///
  /// UTILITY
  ///

  def asJavaDuration(finiteDuration: FiniteDuration): JDuration = JDuration.ofNanos(finiteDuration.toNanos)
  def asJavaDuration(duration: Duration): JDuration = JDuration.ofNanos(duration.toNanos)
  def asJavaCompletionStage[T](future: Future[T]): CompletionStage[T] = scala.compat.java8.FutureConverters.toJava(future)

  def asJavaOptional[T](option: Option[T]): JOptional[T] = scala.compat.java8.OptionConverters.toJava(option)
  def asJavaBoolean(boolean: Boolean): JBoolean = boolean2Boolean(boolean)
  def asJavaLong(long: Long): JLong = long2Long(long)

  // COLLECTIONS

  def asJavaIterable[T](iterable: Iterable[T]): JIterable[T] = JavaConverters.asJavaIterable(iterable)
  def asScalaIterator[T](iterator: JIterator[T]): Iterator[T] = JavaConverters.asScalaIterator(iterator)
  def asScalaList[T](list: JList[T]): List[T] = list.asScala.toList

  // FUNCTIONS

  def asJFluxSink[T](jfluxSink: JFluxSink[T]): FluxSink[T] = FluxSink.wrap(jfluxSink)

  // Known as a Runnable in the java world
  def asJavaRunnable(runnable: () => Unit): JRunnable = () => runnable.apply()
  def asJavaCallable[T](callable: () => T): JCallable[T] = () => callable.apply()

  // Known as a Consumer in the java world
  def asJavaConsumer[T](consumer: T => Unit): JConsumer[T] = (t: T) => consumer.apply(t)

  // Known as a Consumer in the java world
  def asJavaBooleanSupplier(supplier: () => Boolean): JBooleanSupplier = () => supplier.apply()
  def asJavaSupplier[T](supplier: () => T): JSupplier[T] = () => supplier.apply()

  // Known as a Predicate in the java world
  def asJavaPredicate[T](predicate: T => Boolean): JPredicate[T] = (t: T) => predicate.apply(t)

  // Known as a Function in the java world
  def asJavaFn1[T, R](function: T => R): JFunction[T, R] = (t: T) => function.apply(t)

  // Known as BiPredicate in the java world
  def asJavaBiConsumer[T, U](biConsumer: (T, U) => Unit): JBiConsumer[T, U] = (t: T, u: U) => biConsumer.apply(t, u)

  // Known as BiPredicate in the java world
  def asJavaBiPredicate[T, U](biPredicate: (T, U) => Boolean): JBiPredicate[T, U] = (t: T, u: U) => biPredicate.apply(t, u)

  // Known as a BiFunction in the java world
  def asJavaFn2[T, U, R](function: (T, U) => R): JBiFunction[T, U, R] = (t: T, u: U) => function.apply(t, u)

  // Does not exist in the java world (without @FunctionalInterface), so follow convention of reactor by converting to function of Array[AnyRef]
  def asJavaFn3[T1, T2, T3, R](function: (T1, T2, T3) => R): JFunction[Array[AnyRef], R] = (arr: Array[AnyRef]) => function.apply(arr(0).asInstanceOf[T1], arr(1).asInstanceOf[T2], arr(2).asInstanceOf[T3])

  // Does not exist in the java world (without @FunctionalInterface), so follow convention of reactor by converting to function of Array[AnyRef]
  def asJavaFn4[T1, T2, T3, T4, R](function: (T1, T2, T3, T4) => R): JFunction[Array[AnyRef], R] = (arr: Array[AnyRef]) => function.apply(arr(0).asInstanceOf[T1], arr(1).asInstanceOf[T2], arr(2).asInstanceOf[T3], arr(3).asInstanceOf[T4])

  // Does not exist in the java world (without @FunctionalInterface), so follow convention of reactor by converting to function of Array[AnyRef]
  def asJavaFn5[T1, T2, T3, T4, T5, R](function: (T1, T2, T3, T4, T5) => R): JFunction[Array[AnyRef], R] = (arr: Array[AnyRef]) => function.apply(arr(0).asInstanceOf[T1], arr(1).asInstanceOf[T2], arr(2).asInstanceOf[T3], arr(3).asInstanceOf[T4], arr(4).asInstanceOf[T5])

  // Does not exist in the java world (without @FunctionalInterface), so follow convention of reactor by converting to function of Array[AnyRef]
  def asJavaFn6[T1, T2, T3, T4, T5, T6, R](function: (T1, T2, T3, T4, T5, T6) => R): JFunction[Array[AnyRef], R] = (arr: Array[AnyRef]) => function.apply(arr(0).asInstanceOf[T1], arr(1).asInstanceOf[T2], arr(2).asInstanceOf[T3], arr(3).asInstanceOf[T4], arr(4).asInstanceOf[T5], arr(5).asInstanceOf[T6])

}
