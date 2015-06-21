package tk.bad_rabbit.rcam.distributed_backend.command;

import java.nio.CharBuffer;
import java.util.concurrent.Callable;

public interface ICommand extends  Callable<CommandResult>{

  public CharBuffer asCharBuffer();
  public Boolean isIgnored();
  public String getCommandName();
  public Integer getAckNumber();
  
  public ICommand wasAcked();
  public ICommand wasReceived();
}
