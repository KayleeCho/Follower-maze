package maze.model

import maze.model.Message.{EventSequence, Payload, UserId}

object Message {
  type UserId = Long
  type EventSequence = Long
  type Payload = String
}

trait Message {
  val sequence: EventSequence
  val payload: Payload
}
case class Broadcast(sequence: EventSequence, payload: Payload) extends Message
case class Follow(sequence: EventSequence,
                  fromUser: UserId,
                  toUser: UserId,
                  payload: Payload)
  extends Message
case class Unfollow(sequence: EventSequence,
                    fromUser: UserId,
                    toUser: UserId,
                    payload: Payload)
  extends Message
case class PrivateMessage(sequence: EventSequence,
                          fromUser: UserId,
                          toUser: UserId,
                          payload: Payload)
  extends Message
case class StatusUpdate(sequence: EventSequence,
                        fromUser: UserId,
                        payload: Payload)
  extends Message

