package tk.bad_rabbit.rcam.distributed_backend.controller;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.ICommand;
import tk.bad_rabbit.rcam.distributed_backend.command.state.AckedState;
import tk.bad_rabbit.rcam.distributed_backend.command.state.ErrorCommandState;
import tk.bad_rabbit.rcam.distributed_backend.command.state.ICommandState;
import tk.bad_rabbit.rcam.distributed_backend.commandfactory.CommandFactory;
import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.IConfigurationProvider;

public class Controller implements Runnable, Observer {
  
  boolean running;
  ExecutorService commandExecutor;

  CommandFactory commandFactory;
  ConcurrentHashMap<Integer, ACommand> commandList;

  
  
  public Controller(IConfigurationProvider configurationProvider) {
    this.commandList = new ConcurrentHashMap<Integer, ACommand>();
    commandExecutor = Executors.newFixedThreadPool(5);

    commandFactory = new CommandFactory(configurationProvider.getCommandConfigurations(), 
        configurationProvider.getServerVariables(), configurationProvider);
    
  }
  
  public void observeCommand(ACommand command) {
     command.addObserver(this);
  }
  
  public void runCommand(ACommand command) {
    System.out.println("RCam Distributed Backend - Controller - Going to run command " + command.getCommandName() + "[" + command.getAckNumber() + "]");
    commandExecutor.submit(command);
  }
  
  public void cancelCommand(ACommand commandToCancel) {
    commandExecutor.submit(commandToCancel);
  }
  
  public void run() {
    running = true;
    ICommand command;
    while(running) {
      try {
        Thread.sleep(125);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
  
  public void update(Observable updatedCommand, Object arg) {    
    if(! (commandList.containsKey(((ACommand) updatedCommand).getAckNumber()))) {
      commandList.put(((ACommand) updatedCommand).getAckNumber(), (ACommand) updatedCommand);
    }
    
    ((ACommand) updatedCommand).doRelatedCommandAction(this, (ICommandState) arg);
  }

  public void removeCommand(ACommand actionSubject) {
    this.commandList.remove(actionSubject.getAckNumber());
  }

  public void commandResultReceived(int parseInt, String clientVariable) {
    
  }
  
  public void ackCommandReceived(Integer ackNumber) {
    commandList.get(ackNumber).setState(new AckedState());
  }
  
  public void cancelCommandReceived(Integer ackNumber) {
    commandList.get(ackNumber).setState(new ErrorCommandState());
  }
 
}
