package maze.service

import maze.model.Message

import scala.collection.mutable


class EventQueueManager {
  private val messagesBySeqNo = new mutable.HashMap[Long, Message]
  private var lastSeqNo = 0L

  def push(message: Message): Unit = {
    messagesBySeqNo += message.sequence -> message
  }

  def hasNext: Boolean = {
    messagesBySeqNo.contains(lastSeqNo + 1L)
  }

  def pop: Option[Message] = {
    val message = messagesBySeqNo.get(lastSeqNo + 1L)
    if (message.isDefined) {
      messagesBySeqNo -= lastSeqNo + 1L
      lastSeqNo += 1L
    }
    message
  }
}