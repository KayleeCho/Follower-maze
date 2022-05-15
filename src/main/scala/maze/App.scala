package maze

import maze.domain.DeadLetterQueue
import maze.listener.{ClientListener, EventListener}
import maze.router.MessageRouter
import maze.service.{ClientRegistry, EventQueueManager}

object App {

  def main(args: Array[String]): Unit = {
    val deadLetterQueue = new DeadLetterQueue
    val clientRegistry = new ClientRegistry(deadLetterQueue)
    val clientListener = new ClientListener(clientRegistry)
    val messageQueueManager = new EventQueueManager
    val messageRouter = new MessageRouter(deadLetterQueue, messageQueueManager)
    val eventListener = new EventListener(messageRouter)

    clientListener.run
    eventListener.run
  }
}
