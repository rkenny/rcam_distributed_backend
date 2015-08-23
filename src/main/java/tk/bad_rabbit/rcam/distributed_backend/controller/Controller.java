package tk.bad_rabbit.rcam.distributed_backend.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.ICommand;
import tk.bad_rabbit.rcam.distributed_backend.command.Pair;
import tk.bad_rabbit.rcam.distributed_backend.command.state.AckedState;
import tk.bad_rabbit.rcam.distributed_backend.command.state.ICommandState;
import tk.bad_rabbit.rcam.distributed_backend.commandfactory.CommandFactory;
import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.IConfigurationProvider;

public class Controller implements Runnable, Observer{
  
  boolean running;
  ExecutorService commandExecutor;
//  List<Future<Pair<Integer, Integer>>> commandResults; // commands return 'true' for success, 'false' for fail
  CommandFactory commandFactory;
  ConcurrentHashMap<Integer, ACommand> commandList;

  
  
  public Controller(IConfigurationProvider configurationProvider) {
    this.commandList = new ConcurrentHashMap<Integer, ACommand>();
    commandExecutor = Executors.newFixedThreadPool(5);
//    commandResults = new ArrayList<Future<Pair<Integer, Integer>>>();
    commandFactory = new CommandFactory(configurationProvider.getCommandConfigurations(), 
        configurationProvider.getServerVariables(), configurationProvider);
    
  }
  
  public void observeCommand(ACommand command) {
     command.addObserver(this);
  }
  
  public void runCommand(ACommand command) {
    commandExecutor.submit(command);
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
//      while((command = commandQueuer.getNextReadyToExecuteCommand()) != null) {
//        if(!command.isIgnored()) {
//          commandResults.add(commandExecutor.submit(command.setDone()));
//        } else {
//          command.setDone();
//        }
//      }
      
//      Iterator<Future<Pair<Integer, Integer>>> resultIterator = commandResults.iterator();
//      while(resultIterator.hasNext()) {
//        Future<Pair<Integer, Integer>> commandResult = resultIterator.next();
//        try {
          //ICommand returnCommand = commandFactory.createResultCommand(commandResult.get());
//          commandQueuer.addOutgoingCommand(returnCommand.readyToSend());
//        } catch (InterruptedException e) {
//          e.printStackTrace();
//        } catch (ExecutionException e) {
//          e.printStackTrace();
//        }
//        resultIterator.remove();
//      }
//    }
//    commandExecutor.shutdown();
  }
  
  public void update(Observable updatedCommand, Object arg) {
    if(! (commandList.containsKey(((ACommand) updatedCommand).getAckNumber()))) {
      commandList.put(((ACommand) updatedCommand).getAckNumber(), (ACommand) updatedCommand);
    }
    ((ACommand) updatedCommand).doAction(this, (ICommandState) arg);
  }

  public void removeCommand(ACommand actionSubject) {
    this.commandList.remove(actionSubject.getAckNumber());
  }

  public void commandResultReceived(int parseInt, String clientVariable) {
    
  }
  
  public void ackCommandReceived(Integer ackNumber) {
    System.out.println("Ack command received for " + ackNumber);
    commandList.get(ackNumber).setState(new AckedState());
  }
 
}
