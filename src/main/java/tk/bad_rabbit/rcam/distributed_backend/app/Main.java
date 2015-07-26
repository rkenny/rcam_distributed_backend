package tk.bad_rabbit.rcam.distributed_backend.app;

import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.ConfigurationProvider;
import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.IConfigurationProvider;
import tk.bad_rabbit.rcam.distributed_backend.controller.Controller;
import tk.bad_rabbit.rcam.distributed_backend.server.Server;

public class Main {
  static Server server;
  
  static Controller commandController;
  
  public static void main(String[] args) {
    IConfigurationProvider configurationProvider = new ConfigurationProvider();
    commandController = new Controller(configurationProvider);
    server = new Server(configurationProvider.getServerPort(), commandController, configurationProvider);
    server.startServerThread();
      
  }
  
  

}
