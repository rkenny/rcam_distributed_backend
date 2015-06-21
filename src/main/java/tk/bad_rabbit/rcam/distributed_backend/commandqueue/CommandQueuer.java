package tk.bad_rabbit.rcam.distributed_backend.commandqueue;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import tk.bad_rabbit.rcam.distributed_backend.command.CommandState;
import tk.bad_rabbit.rcam.distributed_backend.command.ICommand;

public class CommandQueuer implements ICommandQueuer {

  Map<Integer, ICommand> incomingCommandsMap = null;
  Map<Integer, ICommand> outgoingCommandsMap = null;
  
  Queue<ICommand> incomingCommandsQueueOld = null;
  Queue<ICommand> outgoingCommandsQueueOld = null;
  
  public CommandQueuer() {
    incomingCommandsMap = Collections.synchronizedMap(new LinkedHashMap<Integer, ICommand>());
    outgoingCommandsMap = Collections.synchronizedMap(new LinkedHashMap<Integer, ICommand>());
  }

  public void addIncomingCommand(ICommand command) {
    incomingCommandsMap.put(command.getAckNumber(), command);
  }

  public void addOutgoingCommand(ICommand command) {
    outgoingCommandsMap.put(command.getAckNumber(), command);
  }

  public ICommand getNextReadyToSendOutgoingCommand() {
    synchronized(outgoingCommandsMap) {
      Collection<ICommand> commands =  outgoingCommandsMap.values();
      Iterator<ICommand> i = commands.iterator();
      ICommand command;
      if(i.hasNext()) {
        command = i.next();
        if(command.isReadyToSend()) {
          return command;
        }
      }
      return null;
    }
  }
  
  public ICommand getNextOutgoingCommand(CommandState state) {
    synchronized(outgoingCommandsMap) {
      Collection<ICommand> commands =  outgoingCommandsMap.values();
      Iterator<ICommand> i = commands.iterator();
      ICommand command;
      if(i.hasNext()) {
        command = i.next();
        if(command.isInState(state)) {
          return command;
        }
        i.remove();
      }
      return null;
    }
  }

  public ICommand getNextIncomingCommand(CommandState state) {
    synchronized(incomingCommandsMap) {
      Collection<ICommand> commands =  incomingCommandsMap.values();
      Iterator<ICommand> i = commands.iterator();
      ICommand command;
      if(i.hasNext()) {
        command = i.next();
        if(command.isInState(state)) {
          return command;
        }
        i.remove();
      }
      return null;
    }
  }
  
  public ICommand getNextReadyToExecuteCommand() {
    synchronized(incomingCommandsMap) {
      Collection<ICommand> commands =  incomingCommandsMap.values();
      Iterator<ICommand> i = commands.iterator();
      ICommand command;
      if(i.hasNext()) {
        command = i.next();
        if(command.isReadyToExecute()) {
          return command;
        }
        i.remove();
      }
      return null;
    }
  }

  public void flushQueues() {
    outgoingCommandsMap.clear();
    incomingCommandsMap.clear();
  }
  
}
