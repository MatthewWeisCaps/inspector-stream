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