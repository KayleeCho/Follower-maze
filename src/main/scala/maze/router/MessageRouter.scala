package maze.router

import maze.domain.{DeadLetterQueue, EventError, ParseError}
import maze.model.Message.UserId
import maze.model._
import maze.service._

import scala.collection.mutable

class MessageRouter(deadLetterQueue: DeadLetterQueue,
                    eventQueueManager: EventQueueManager) {

  val clientRegistry = new ClientRegistry(deadLetterQueue)

  def handle(payload: String): Unit =
    parseEvent(payload).fold(error => ParseError(s"invalid message"),
                             message => sendMessage(message))

  private def parseEvent(payload: String): Either[ParseError, Message] = {
    val parts = payload.split("\\|")
    parse(parts, payload)
  }

  private def parse(parts: Array[String],
                    payload: String): Either[ParseError, Message] = {
    parts(1) match {
      case "F" =>
        Right(
          Follow(parts(0).toLong, parts(2).toLong, parts(3).toLong, payload))
      case "U" =>
        Right(
          Unfollow(parts(0).toLong, parts(2).toLong, parts(3).toLong, payload))
      case "P" =>
        Right(
          PrivateMessage(parts(0).toLong,
                         parts(2).toLong,
                         parts(3).toLong,
                         payload))
      case "S" => Right(StatusUpdate(parts(0).toLong, parts(2).toLong, payload))
      case "B" => Right(Broadcast(parts(0).toLong, payload))
      case _ =>
        Left(ParseError(s"invalid message, receieved-> $parts"))
    }
  }

  private def sendMessage(message: Message): Unit =
    message match {
      case Follow(eventSequence, fromUserId, toUserId, payload) =>
        eventQueueManager.push(message)
        follow(Follow(eventSequence, fromUserId, toUserId, payload))
      case Unfollow(eventSequence, fromUserId, toUserId, payload) =>
        eventQueueManager.push(message)
        unfollow(Unfollow(eventSequence, fromUserId, toUserId, payload))
      case PrivateMessage(eventSequence, fromUserId, toUserId, payload) =>
        eventQueueManager.push(message)
        privateMessage(
          PrivateMessage(eventSequence, fromUserId, toUserId, payload))
      case Broadcast(eventSequence, payload) =>
        eventQueueManager.push(message)
        broadcast(Broadcast(eventSequence, payload))
      case StatusUpdate(eventSequence, fromUserId, payload) =>
        eventQueueManager.push(message)
        statusUpdate(StatusUpdate(eventSequence, fromUserId, payload))

    }

  val followersRegistry = new mutable.HashMap[UserId, Set[UserId]]
  private def follow(follow: Follow): Either[EventError, Unit] = {
    val followersOfUser = followersRegistry.getOrElse(follow.toUser, Set.empty)
    val newFollowers = followersOfUser + follow.fromUser
    followersRegistry.put(follow.toUser, newFollowers)
    clientRegistry.withUserOnline(follow.toUser, follow.payload) { protocol =>
      protocol.write(follow.payload)
    }
  }

  private def unfollow(unfollow: Unfollow): Unit = {
    val followers = followersRegistry.getOrElse(unfollow.toUser, Set.empty)
    val newFollowers = followers - unfollow.fromUser
    followersRegistry.put(unfollow.toUser, newFollowers)
  }

  private def privateMessage(
      privateMessage: PrivateMessage): Either[EventError, Unit] =
    clientRegistry.withUserOnline(privateMessage.toUser, privateMessage.payload) {
      protocol =>
        protocol.write(privateMessage.payload)
    }

  private def broadcast(broadcast: Broadcast): List[Either[EventError, Unit]] = {
    val clientPool = clientRegistry.clientPool
    clientPool.values.map { socket =>
      socket.write(broadcast.payload)
    }
  }.toList

  private def statusUpdate(
      statusUpdate: StatusUpdate): List[Either[EventError, Unit]] = {

    val followers =
      followersRegistry.get(statusUpdate.fromUser).getOrElse(Set.empty)
    followers.toList.map { follower =>
      clientRegistry.withUserOnline(follower, statusUpdate.payload) { socket =>
        socket.write(statusUpdate.payload)
      }
    }
  }
}
