package tk.bad_rabbit.rcam.distributed_backend.controller;

import java.util.Map;
import java.util.Observable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.ICommand;
import tk.bad_rabbit.rcam.distributed_backend.command.action.IActionHandler;
import tk.bad_rabbit.rcam.distributed_backend.command.action.ICommandAction;
import tk.bad_rabbit.rcam.distributed_backend.command.action.IRunResponseAction;

public class RunController implements Runnable, IActionHandler {
  
  boolean running;
  ExecutorService commandExecutor;

  ConcurrentHashMap<Integer, ACommand> commandList;  
  
  public RunController() {
    this.commandList = new ConcurrentHashMap<Integer, ACommand>();
    commandExecutor = Executors.newFixedThreadPool(5);
    
  }
  
  public Future<Map.Entry<Integer, Integer>> runCommand(ACommand command) {
    System.out.println("RCam Distributed Backend - RunController - Running Command("+command.getCommandName()+"["+command.getAckNumber()+"])");
    return commandExecutor.submit(command.getCallable(command.getState().getClass().getSimpleName()));
  }
  
  public void cancelCommand(ACommand command) {
    commandExecutor.submit(command.getCallable(command.getState().getClass().getSimpleName()));
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
  
  public void removeCommand(ACommand actionSubject) {
    this.commandList.remove(actionSubject.getAckNumber());
  }
  
  public ACommand getCommand(Integer ackNumber) {
    return this.commandList.get(ackNumber);
  }

  public Future<Integer> handleAction(ICommandAction action) {
    if(action instanceof IRunResponseAction) {
      return commandExecutor.submit( ((IRunResponseAction) action).getRunCallable(this));
    }
    return null;
  }
  
}
