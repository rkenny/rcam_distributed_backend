package tk.bad_rabbit.rcam.distributed_backend.command;

import java.nio.CharBuffer;
import java.util.Observer;
import java.util.concurrent.Callable;

import tk.bad_rabbit.rcam.distributed_backend.command.state.ICommandState;

public interface ICommand extends  Callable<Pair<Integer, Integer>>{

  public CharBuffer asCharBuffer();
  public Boolean isIgnored();
  public String getCommandName();
  public Integer getAckNumber();
  
  
  public String getCommandVariable(String variableName);
  public Object getClientVariable(String variableName);
  public String getServerVariable(String variableName);

  public void performCommandResponseAction(Object actionObject);
  
  public Boolean isType(String commandType);
  public ICommandState getState();
  
  //public ACommand copy();
  public ICommandState setState(ICommandState state);
  
  public void setReturnCode(String returnCode);
  public String getReturnCode();

  public void doAction(Observer actionObserver, ICommandState commandState);

  
}
