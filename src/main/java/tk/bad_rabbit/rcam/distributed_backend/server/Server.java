package tk.bad_rabbit.rcam.distributed_backend.server;

import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.state.AckedState;
import tk.bad_rabbit.rcam.distributed_backend.command.state.AwaitingAckState;
import tk.bad_rabbit.rcam.distributed_backend.commandfactory.CommandFactory;
import tk.bad_rabbit.rcam.distributed_backend.commandfactory.ICommandFactory;
import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.IConfigurationProvider;
import tk.bad_rabbit.rcam.distributed_backend.controller.Controller;

public class Server  {
  int port;
  
  ServerSocketChannel serverSocketChannel;
  SocketChannel socketChannel;
  Selector serverSelector;
  
  CharsetDecoder asciiDecoder;
  CharsetEncoder asciiEncoder;
  
  
  ICommandFactory commandFactory;
  IConfigurationProvider configurationProvider;
  ServerThread serverThread;
  Controller controller;
  
  public Server(int port, Controller controller, IConfigurationProvider configurationProvider) {
    this();
    this.port = port;
    this.controller = controller;
    this.configurationProvider = configurationProvider;
    this.commandFactory = new CommandFactory(this.configurationProvider.getCommandConfigurations(), 
        this.configurationProvider.getCommandVariables(), this.configurationProvider.getServerVariables(), configurationProvider);
    this.serverThread = new ServerThread(commandFactory, controller, port);
  }
  
  public Server() {
    asciiDecoder = Charset.forName("US-ASCII").newDecoder();
    asciiEncoder = Charset.forName("US-ASCII").newEncoder();
    
  }

  public void startServerThread() {
    serverThread.start();
  }
  
  public void observeCommand(ACommand command) {
    this.serverThread.observeCommand(command);
  }
  
  public void send(ACommand command) {
    sendCommand(command);
    command.setState(new AwaitingAckState());
  }

  private void sendCommand(ACommand command) {
    synchronized(command) {
      serverThread.send(command);
    }
  }
  
 
  
}
