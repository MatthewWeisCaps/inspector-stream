package org.sireum.hamr.inspector.stream

import org.reactivestreams.{Processor, Subscription}
import reactor.core.publisher.{MonoProcessor => JMonoProcessor}
import reactor.core.{Disposable, Scannable => JScannable}
import reactor.util.context.Context

import scala.collection.JavaConverters._
import scala.concurrent.duration.Duration

class MonoProcessor[O](private val processorDelegate: JMonoProcessor[O]) extends Mono[O] with Processor[O, O] with Disposable with Subscription with Scannable {

  override private[stream] val delegate: JMonoProcessor[O] = processorDelegate
  override private[stream] def jscannable: JScannable = processorDelegate

  override def block(): O = super.block()

  override def block(timeout: Duration): O = super.block(timeout)

  override def cancel(): Unit = delegate.cancel()

  def currentContext: Context = delegate.currentContext()

  override def dispose(): Unit = delegate.dispose()

  def downstreamCount: Long = delegate.downstreamCount()

  def getError: Option[Throwable] = Option(delegate.getError)

  def hasDownstreams: Boolean = delegate.hasDownstreams

  override def inners: Stream[Scannable] = delegate.inners.iterator().asScala.toStream.map(scannable => new Scannable {
    override private[stream] def jscannable = scannable
  })

  def isCancelled: Boolean = delegate.isCancelled
  override def isDisposed: Boolean = delegate.isDisposed
  def isError: Boolean = delegate.isError
  def isSuccess: Boolean = delegate.isSuccess
  def isTerminated: Boolean = delegate.isTerminated

  override def onComplete(): Unit = delegate.onComplete()
  override def onError(cause: Throwable): Unit = delegate.onError(cause)
  override def onNext(value: O): Unit = delegate.onNext(value)
  override def onSubscribe(s: Subscription): Unit = delegate.onSubscribe(s)

  def peek(): Option[O] = Option(delegate.peek)

  override def request(n: Long): Unit = delegate.request(n)

  override def scanUnsafe(key: Scannable.Attr[_]): Option[AnyRef] = Option(delegate.scanUnsafe(key))

}

object MonoProcessor {

  def create[T](): MonoProcessor[T] = new MonoProcessor[T](JMonoProcessor.create())

}