package tk.bad_rabbit.rcam.distributed_backend.app;

import tk.bad_rabbit.rcam.distributed_backend.client.Client;
import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.ConfigurationProvider;
import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.IConfigurationProvider;
import tk.bad_rabbit.rcam.distributed_backend.controller.Controller;

public class Main {
  static Client client;
  
  static Controller commandController;
  
  public static void main(String[] args) {
    IConfigurationProvider configurationProvider = new ConfigurationProvider();
    commandController = new Controller(configurationProvider);
    client = new Client(configurationProvider.getServerAddress(), configurationProvider.getServerPort(), commandController, configurationProvider);
    client.start();
    //server = new Server(configurationProvider.getServerPort(), commandController, configurationProvider);
    //server.startServerThread();
      
  }
  
  

}
