package maze.model

sealed trait EventError
case class ParseError(message: String) extends EventError
case class DeliveryError(message: String) extends EventError