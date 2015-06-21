package tk.bad_rabbit.rcam.distributed_backend.commandqueue;

import tk.bad_rabbit.rcam.distributed_backend.command.CommandState;
import tk.bad_rabbit.rcam.distributed_backend.command.ICommand;

public interface ICommandQueuer {
  public void addIncomingCommand(ICommand command);
  public void addOutgoingCommand(ICommand command);
  
  public ICommand getNextIncomingCommand(CommandState state);
  public ICommand getNextReadyToExecuteCommand();
  
  public ICommand getNextOutgoingCommand(CommandState state);
  public ICommand getNextReadyToSendOutgoingCommand();
  
  public void flushQueues();
}
