package tk.bad_rabbit.rcam.distributed_backend.client;

import tk.bad_rabbit.rcam.distributed_backend.commandcontroller.CommandController;
import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.IConfigurationProvider;
import tk.bad_rabbit.rcam.distributed_backend.controller.RunController;

public class Client {
  int port;
  String address;
  
  ClientThread clientThread;
  RunController runController;
  CommandController commandController;
  
  public Client(IConfigurationProvider configurationProvider) {
    
    this.clientThread = new ClientThread(configurationProvider, new RunController(), new CommandController());
  }
  
  

  public void start() {
    clientThread.start();
  }
  
}
