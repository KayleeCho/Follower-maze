package maze.domain

import scala.collection.mutable
import scala.util.Try

class DeadLetterQueue {
  private val queue = new mutable.Queue[String]

  def push(message: String): Unit = {
    queue.enqueue(message)
  }

  def hasNext: Boolean = {
    queue.nonEmpty
  }

  def pop(): Option[String] = {
    Try(queue.dequeue).toOption
  }

  def popAll: Seq[String] = {
    queue.dequeueAll(_ => true)
  }
}
