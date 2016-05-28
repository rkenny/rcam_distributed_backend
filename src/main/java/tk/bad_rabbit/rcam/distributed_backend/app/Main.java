package tk.bad_rabbit.rcam.distributed_backend.app;

import tk.bad_rabbit.rcam.distributed_backend.client.Client;
import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.ConfigurationProvider;
import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.IConfigurationProvider;
import tk.bad_rabbit.rcam.distributed_backend.controller.RunController;

public class Main {
  static Client client;
  
  
  public static void main(String[] args) {
    
    client = new Client(new ConfigurationProvider());
    client.start();
  }
  
  

}
