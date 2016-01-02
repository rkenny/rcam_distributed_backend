package tk.bad_rabbit.rcam.distributed_backend.client;

import tk.bad_rabbit.rcam.distributed_backend.commandfactory.CommandFactory;
import tk.bad_rabbit.rcam.distributed_backend.commandfactory.ICommandFactory;
import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.IConfigurationProvider;
import tk.bad_rabbit.rcam.distributed_backend.controller.Controller;

public class Client {
  int port;
  String address;

  
  ICommandFactory commandFactory;
  IConfigurationProvider configurationProvider;
  ClientThread clientThread;
  Controller controller;
  

  public Client(String address, int port, Controller controller, IConfigurationProvider configurationProvider) {
    this();
    this.port = port;
    this.controller = controller;
    this.configurationProvider = configurationProvider;
    this.commandFactory = new CommandFactory(this.configurationProvider.getCommandConfigurations(), 
        this.configurationProvider.getServerVariables(), configurationProvider);
    this.clientThread = new ClientThread(address, port, commandFactory, controller);
  }
  
  public Client() {

  }
  

  public void start() {
    clientThread.start();
  }
  
}
