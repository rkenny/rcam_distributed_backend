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
  
  
  public Object getClientVariable(String variableName);
  public Object getServerVariable(String variableName);

  public void performCommandResponseNetworkAction(Observer actionObject);
  public void performCommandResponseRelatedCommandAction(Observer actionObject);
  
  public Boolean isType(String commandType);
  public ICommandState getState();
  
  //public ACommand copy();
  public ICommandState setState(ICommandState state);
  
  public void setReturnCode(String returnCode);
  public String getReturnCode();

  public void doNetworkAction(Observer actionObserver, ICommandState commandState);
  public void doRelatedCommandAction(Observer actionObserver, ICommandState commandState);

  
}
