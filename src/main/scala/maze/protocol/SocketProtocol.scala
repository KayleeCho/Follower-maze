package maze.protocol

import java.io.{BufferedWriter, OutputStreamWriter}
import java.net.Socket

import maze.model.DeliveryError

import scala.util.{Success, Try}


class SocketProtocol(socket: Socket)  {
  def write(payload: String): Either[DeliveryError, Unit] = Try {
    val writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream))
    writer.write(payload + "\n")
    writer.flush()
  } match {
    case Success(value) => Right()
    case _ => Left(DeliveryError("failed to be delivered"))
  }
}
