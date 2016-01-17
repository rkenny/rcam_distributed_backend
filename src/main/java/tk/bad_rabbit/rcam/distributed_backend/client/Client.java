package tk.bad_rabbit.rcam.distributed_backend.client;

import tk.bad_rabbit.rcam.distributed_backend.commandfactory.CommandFactory;
import tk.bad_rabbit.rcam.distributed_backend.commandfactory.ICommandFactory;
import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.IConfigurationProvider;
import tk.bad_rabbit.rcam.distributed_backend.controller.Controller;

public class Client {
  int port;
  String address;
  
  ICommandFactory commandFactory;
  ClientThread clientThread;
  Controller controller;
  

  public Client(IConfigurationProvider configurationProvider) {
    
    this.commandFactory = new CommandFactory(configurationProvider);
    this.clientThread = new ClientThread(configurationProvider,
                                          commandFactory, 
                                          new Controller());
  }
  
  

  public void start() {
    clientThread.start();
  }
  
}
