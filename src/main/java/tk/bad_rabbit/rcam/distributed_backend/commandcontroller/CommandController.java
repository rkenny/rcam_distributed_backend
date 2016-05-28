package tk.bad_rabbit.rcam.distributed_backend.commandcontroller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.action.IActionHandler;
import tk.bad_rabbit.rcam.distributed_backend.command.action.ICommandAction;
import tk.bad_rabbit.rcam.distributed_backend.command.action.IRelatedCommandAction;

public class CommandController implements IActionHandler {

  ExecutorService commandExecutor;
  Map<Integer, ACommand> commandMap;
  
  public CommandController() {
    this.commandMap = new HashMap<Integer, ACommand>();
    this.commandExecutor = Executors.newFixedThreadPool(2);
  }
  
  public void addCommand(ACommand command) {
    this.commandMap.put(command.getAckNumber(), command);
  }
  
  public ACommand getCommand(Integer ackNumber) {
    return this.commandMap.get(ackNumber);
  }
  
  public Future<Integer> handleAction(ICommandAction action) {
    if(action instanceof IRelatedCommandAction) {
      return commandExecutor.submit(((IRelatedCommandAction) action).getRelatedCallable(this)); 
    }
    return null;
  }

}
