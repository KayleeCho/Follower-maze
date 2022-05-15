package maze.service

import maze.model.{Broadcast, Follow, Message, ParseError, PrivateMessage, StatusUpdate, Unfollow}

class MessageParser {
  def parseEvent(payload: String): Either[ParseError, Message] = {
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
}
