package tk.bad_rabbit.rcam.distributed_backend.command;

import java.nio.CharBuffer;
import java.util.List;
import java.util.Map;
import java.util.Observer;
import java.util.concurrent.Callable;

import tk.bad_rabbit.rcam.distributed_backend.command.state.ACommandState;
import tk.bad_rabbit.rcam.distributed_backend.command.state.ICommandState;

public interface ICommand {

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
  public ACommandState getState();
  
  //public ACommand copy();
  public ACommandState setState(ACommandState state);
  public void nextState();
  
  public void setReturnCode(String returnCode);
  public String getReturnCode();

  public void addObservers(List<Observer> observers);
  
  public Callable<Map.Entry<Integer, Integer>> getCallable(String executable);
  
  //public void doNetworkAction(Observer actionObserver, ICommandState commandState);
  //public void doRelatedCommandAction(Observer actionObserver, ICommandState commandState);

  
}
