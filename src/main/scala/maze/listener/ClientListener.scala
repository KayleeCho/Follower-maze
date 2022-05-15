package maze.listener

import java.io.{BufferedReader, InputStreamReader}
import java.net.ServerSocket

import maze.protocol.SocketProtocol
import maze.service.ClientRegistry

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
class ClientListener(clientService: ClientRegistry) {

  private val ClientPort = 9099
  def run =
    Future {
      System.out.println("Listening for client requests on " + ClientPort)
      val serverSocket = new ServerSocket(ClientPort)
      var clientSocket = serverSocket.accept()
      while (clientSocket != null) {
        val reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream))
        val userId = reader.readLine()
        if (userId != null) {
          clientService.add(userId.toLong, new SocketProtocol(clientSocket))
        }
        clientSocket = serverSocket.accept
      }

    }
}
