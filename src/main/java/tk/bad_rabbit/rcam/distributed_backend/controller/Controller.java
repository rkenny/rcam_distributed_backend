package tk.bad_rabbit.rcam.distributed_backend.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import tk.bad_rabbit.rcam.distributed_backend.command.CommandResult;
import tk.bad_rabbit.rcam.distributed_backend.command.ICommand;
import tk.bad_rabbit.rcam.distributed_backend.commandfactory.CommandFactory;
import tk.bad_rabbit.rcam.distributed_backend.commandqueue.ICommandQueuer;
import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.IConfigurationProvider;

public class Controller implements Runnable{
  ICommandQueuer commandQueuer;
  boolean running;
  ExecutorService commandExecutor;
  List<Future<CommandResult>> commandResults; // commands return 'true' for success, 'false' for fail
  CommandFactory commandFactory;
  
  public Controller(ICommandQueuer commandQueuer, IConfigurationProvider configurationProvider) {
    this.commandQueuer = commandQueuer;
    commandExecutor = Executors.newFixedThreadPool(5);
    commandResults = new ArrayList<Future<CommandResult>>();
    commandFactory = new CommandFactory(configurationProvider.getCommandConfigurations(), 
        configurationProvider.getCommandVariables(), configurationProvider.getServerVariables());
    
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
      
      while((command = commandQueuer.getNextIncomingCommand()) != null) {
        commandResults.add(commandExecutor.submit(command));
      }
      
      Iterator<Future<CommandResult>> resultIterator = commandResults.iterator();
      while(resultIterator.hasNext()) {
        Future<CommandResult> commandResult = resultIterator.next();
        try {
          CommandResult result = commandResult.get();
          System.out.println("Result: " + result);
          commandQueuer.addOutgoingCommand(result);
        } catch (InterruptedException e) {
          e.printStackTrace();
        } catch (ExecutionException e) {
          e.printStackTrace();
        }
        resultIterator.remove();
      }
    }
    commandExecutor.shutdown();
  }
 
}
