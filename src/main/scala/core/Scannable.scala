package core

import reactor.core.{Scannable => JScannable}

import scala.collection.JavaConverters._

trait Scannable {

  private[core] def jscannable: JScannable

  def actuals: Stream[Scannable] = jscannable.actuals.iterator().asScala.toStream.map(scannable => new Scannable {
    override private[core] def jscannable = scannable
  })

  def inners: Stream[Scannable] = jscannable.inners.iterator().asScala.toStream.map(scannable => new Scannable {
    override private[core] def jscannable = scannable
  })

  def isScanAvailable: Boolean = jscannable.isScanAvailable

  def name: String = jscannable.name

  def parents: Stream[Scannable] = jscannable.parents.iterator().asScala.toStream.map(scannable => new Scannable {
    override private[core] def jscannable = scannable
  })

  def scan[T](key: JScannable.Attr[T]): Option[T] = Option(jscannable.scan[T](key))

  def scanOrDefault[T](key: JScannable.Attr[T], defaultValue: T): T = jscannable.scanOrDefault(key, defaultValue)

  def scanUnsafe(key: JScannable.Attr[_]): AnyRef = Option(jscannable.scanUnsafe(key))

  def stepName: String = jscannable.stepName

  def steps: Stream[String] = jscannable.steps.iterator().asScala.toStream

  def tags: Stream[(String, String)] = jscannable.tags.iterator().asScala.toStream.map(JavaInterop.toScalaTuple2)

}

object Scannable {
  /**
    *
    *
    * Credit: added Scannable match case after reading reactor-scala-extension's implementation at:
    * https://github.com/reactor/reactor-scala-extensions/blob/master/src/main/scala/reactor/core/scala/Scannable.scala#L91
    * todo add their license + notice
    *
    * @param any
    * @return
    */
  protected[core] def from(any: Option[AnyRef]): Scannable = any match {
    case None => new Scannable {
      override private[core] def jscannable: JScannable = JScannable.from(null)
    }
    case Some(scannable: Scannable) => new Scannable {
      override private[core] def jscannable: JScannable = JScannable.from(scannable.jscannable)
    }
    case Some(value) => new Scannable {
      override private[core] def jscannable: JScannable = JScannable.from(value)
    }
  }

  protected[core] type Attr[T] = JScannable.Attr[T]

}
