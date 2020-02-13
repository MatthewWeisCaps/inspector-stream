package org.sireum.hamr.inspector.stream

import org.reactivestreams.{Processor, Publisher, Subscriber, Subscription}
import reactor.core.Disposable
import reactor.core.publisher.{FluxProcessor => JFluxProcessor}
import JavaInterop._

trait FluxProcessor[IN, OUT]/*(private val processorDelegate: JFluxProcessor[IN, OUT])*/ extends Flux[OUT] with Processor[IN, OUT] /*with Publisher[OUT] with Subscriber[IN]*/ with Disposable with Scannable {

  override private[stream] val delegate: JFluxProcessor[IN, OUT]
  override private[stream] val jscannable = delegate

  override def dispose(): Unit = delegate.dispose()

  def downstreamCount: Long = delegate.downstreamCount()

  def getBufferSize: Int = delegate.getBufferSize

  def getError: Option[Throwable] = Option(delegate.getError)

  def hasCompleted: Boolean = delegate.hasCompleted

  def hasDownstreams: Boolean = delegate.hasDownstreams

  def hasError: Boolean = delegate.hasDownstreams

  override def isDisposed: Boolean = delegate.isDisposed

  override def inners: Stream[Scannable] = asScalaIterator(delegate.inners.iterator()).toStream.map(scannable => new Scannable {
    override private[stream] def jscannable = scannable
  })

  def isSerialized: Boolean = delegate.isSerialized
  def isTerminated: Boolean = delegate.isTerminated

  override def scanUnsafe(key: Scannable.Attr[_]): Option[AnyRef] = Option(delegate.scanUnsafe(key))

  def serialize(): FluxProcessor[IN, OUT] = FluxProcessor.create[IN, OUT](delegate.serialize())

  def sink(): FluxSink[IN] = FluxSink.wrap(delegate.sink())
  def sink(strategy: FluxSink.OverflowStrategy): FluxSink[IN] = FluxSink.wrap(delegate.sink(strategy))

  override def onSubscribe(s: Subscription): Unit = delegate.onSubscribe(s)

  override def onNext(t: IN): Unit = delegate.onNext(t)

  override def onError(t: Throwable): Unit = delegate.onError(t)

  override def onComplete(): Unit = delegate.onComplete()
}

object FluxProcessor {

  def switchOnNext[T]: FluxProcessor[Publisher[_ <: T], T] = new FluxProcessor[Publisher[_ <: T], T] {
    override private[stream] val delegate = JFluxProcessor.switchOnNext[T]()
  }

  /**
    * Credit: Fixed implementation by using design from:
    * https://github.com/reactor/reactor-scala-extensions/blob/master/src/main/scala/reactor/core/scala/publisher/FluxProcessor.scala#L184
    * @param upstream
    * @param downstream
    * @tparam IN
    * @tparam OUT
    * @return
    */
  def wrap[IN, OUT](upstream: Subscriber[IN], downstream: Publisher[OUT]): FluxProcessor[IN, OUT] = create[IN, OUT](JFluxProcessor.wrap(upstream, downstream))

  private def create[IN, OUT](jprocessor: JFluxProcessor[IN, OUT]): FluxProcessor[IN, OUT] = {
    new FluxImpl[OUT](jprocessor) with FluxProcessor[IN, OUT] {
      override private[stream] val delegate = jprocessor
    }
  }
}