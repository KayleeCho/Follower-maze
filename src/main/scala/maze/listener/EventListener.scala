package maze.listener

import java.io.{BufferedReader, InputStreamReader}
import java.net.ServerSocket

import maze.router._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Try}
class EventListener(messageRouter: MessageRouter) {

  private val EventPort = 9090

  def run: Future[Unit] =
    Future {
      System.out.println("Listening for events on " + EventPort)
      val eventSocket = new ServerSocket(EventPort).accept()

      val readerStart = Try {
        new BufferedReader(new InputStreamReader(eventSocket.getInputStream()))
      }

      readerStart match {
        case Success(reader) =>
          Iterator
            .continually(reader.readLine())
            .takeWhile(null != _)
            .foreach { payload =>
              {
                println(s"Message received: $payload") //
                messageRouter.handle(payload)
              }
            }
        case _ =>
          throw new RuntimeException("something went wrong reading message")
      }
    }
}
