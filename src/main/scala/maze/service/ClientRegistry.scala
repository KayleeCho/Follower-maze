package maze.service

import maze.domain.{DeadLetterQueue, DeliveryError, EventError}
import maze.model.DeliveryError
import maze.protocol._

import scala.collection.concurrent.TrieMap

class ClientRegistry(deadLetterQueue: DeadLetterQueue) {

  val clientPool = new TrieMap[Long, SocketProtocol]

  def add(userId: Long, protocol: SocketProtocol) = {
    clientPool.put(userId, protocol)
  }
  def withUserOnline[A,B](userId: Long, payload: String)(write: SocketProtocol => Either[EventError, Unit]): Either[EventError, Unit] = {
    clientPool.get(userId).map(write) match {
      case Some(socket) => socket
      case None =>
        deadLetterQueue.push(payload)
        Left(DeliveryError("User is Not online"))
    }
  }

  def broadcast(payload: String) = {
    clientPool.values.map { socket => socket.write(payload) }
  }
}
