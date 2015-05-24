package tk.bad_rabbit.rcam.distributed_backend.app;

import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.IConfigurationProvider;
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
    IConfigurationProvider configurationProvider = new ConfigurationProvider();
    startServer(commandQueuer, configurationProvider);
    startController(commandQueuer);  
  }
    
  private static void startController(ICommandQueuer commandQueuer) {
    commandController = new Controller(commandQueuer);
    controllerThread = new Thread(commandController);
    controllerThread.start();
  }
  
  private static void startServer(ICommandQueuer commandQueuer, IConfigurationProvider configurationProvider) {
    server = new Server(configurationProvider.getServerPort(), commandQueuer, configurationProvider);
    serverThread = new Thread(server);
    serverThread.start();
  }

}
