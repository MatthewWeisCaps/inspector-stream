package org.sireum.hamr.inspector.stream

import reactor.core.Disposable
import reactor.core.publisher.{FluxSink => JFluxSink}
import reactor.util.context.Context

trait FluxSink[T] {

  type OverflowStrategy = JFluxSink.OverflowStrategy

  private[stream] def delegate: JFluxSink[T]

  def complete(): Unit = delegate.complete()

  def currentContext(): Context = delegate.currentContext()

  def error(e: Throwable): Unit = delegate.error(e)
  def next(t: T): FluxSink[T] = FluxSink.wrap[T](delegate.next(t))

  def requestedFromDownstream(): Long = delegate.requestedFromDownstream()

  def isCancelled: Boolean = delegate.isCancelled

  def onRequest(consumer: Long => Unit): FluxSink[T] = FluxSink.wrap[T](delegate.onRequest(JavaInterop.asJavaLongConsumer(consumer)))
  def onCancel(d: Disposable): FluxSink[T] = FluxSink.wrap[T](delegate.onCancel(d))
  def onDispose(d: Disposable): FluxSink[T] = FluxSink.wrap[T](delegate.onDispose(d))
}

object FluxSink {

  type OverflowStrategy = JFluxSink.OverflowStrategy

  def wrap[T](jFluxSink: JFluxSink[T]): FluxSink[T] = new FluxSink[T] {
    override private[stream] def delegate: JFluxSink[T] = jFluxSink
  }
}
