package tk.bad_rabbit.rcam.distributed_backend.app;

import tk.bad_rabbit.rcam.distributed_backend.controller.Controller;
import tk.bad_rabbit.rcam.distributed_backend.server.Server;
import tk.bad_rabbit.rcam.distributed_backend.commandqueue.*;

public class Main {
  static Server server;
  static Thread serverThread;
  static Controller commandController;
  static Thread controllerThread;
  public static void main(String[] args) {
    ICommandQueuer commandQueuer = new CommandQueuer();
    startServer(12345, commandQueuer);
    startController(commandQueuer);  
  }
    
  private static void startController(ICommandQueuer commandQueuer) { //ConcurrentLinkedQueue<String> incomingCommandsQueue, ConcurrentLinkedQueue<String> outgoingCommandsQueue, ConcurrentLinkedQueue<ICommand> commandQueuer) {
    commandController = new Controller(commandQueuer);//incomingCommandsQueue, outgoingCommandsQueue, commandQueuer);
    controllerThread = new Thread(commandController);
    controllerThread.start();
  }
  
  private static void startServer(int port, ICommandQueuer commandQueuer) {//ConcurrentLinkedQueue<String> incomingCommandsQueue, ConcurrentLinkedQueue<String> outgoingCommandsQueue) {
    server = new Server(port, commandQueuer);//incomingCommandsQueue, outgoingCommandsQueue);
    serverThread = new Thread(server);
    serverThread.start();
  }

}
