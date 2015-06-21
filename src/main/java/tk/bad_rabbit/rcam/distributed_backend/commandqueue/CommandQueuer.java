package tk.bad_rabbit.rcam.distributed_backend.commandqueue;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import tk.bad_rabbit.rcam.distributed_backend.command.ICommand;

public class CommandQueuer implements ICommandQueuer {
  Queue<ICommand> incomingCommandsQueue = null;
  Queue<ICommand> outgoingCommandsQueue = null;
  
  public CommandQueuer() {
    incomingCommandsQueue = new ConcurrentLinkedQueue<ConcurrentHashMap<Integer, ICommand>>();
    outgoingCommandsQueue = new ConcurrentLinkedQueue<ICommand>();
  }

  public void addIncomingCommand(ICommand command) {
    incomingCommandsQueue.add(command);

  }

  public void addOutgoingCommand(ICommand command) {
    outgoingCommandsQueue.add(command);
  }

  public ICommand getNextOutgoingCommand() {
    return outgoingCommandsQueue.poll();
  }

  public ICommand getNextIncomingCommand() {
    return incomingCommandsQueue.poll();
  }

  public void flushQueues() {
    incomingCommandsQueue.removeAll(incomingCommandsQueue);
    outgoingCommandsQueue.removeAll(outgoingCommandsQueue);
  }
  
}
