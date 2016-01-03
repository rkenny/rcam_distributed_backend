package tk.bad_rabbit.rcam.distributed_backend.command;

import java.nio.CharBuffer;
import java.util.List;
import java.util.Map;
import java.util.Observer;
import java.util.concurrent.Callable;

import tk.bad_rabbit.rcam.distributed_backend.command.state.ICommandState;

public interface ICommand extends  Callable<Map.Entry<Integer, Integer>>{

  public CharBuffer asCharBuffer();
  public Boolean isIgnored();
  public String getCommandName();
  public Integer getAckNumber();
  
  
  public Object getClientVariable(String variableName);
  public Object getServerVariable(String variableName);

  public void doNetworkAction(Observer actionObserver);
  public void doRelatedCommandAction(Observer actionObserver);
  public void doRunCommandAction(Observer actionObserver);
  
  public Boolean isType(String commandType);
  public ICommandState getState();
  
  //public ACommand copy();
  public ICommandState setState(ICommandState state);
  
  public void setReturnCode(String returnCode);
  public String getReturnCode();

  public void addObservers(List<Observer> observers);
  
  //public void doNetworkAction(Observer actionObserver, ICommandState commandState);
  //public void doRelatedCommandAction(Observer actionObserver, ICommandState commandState);

  
}
