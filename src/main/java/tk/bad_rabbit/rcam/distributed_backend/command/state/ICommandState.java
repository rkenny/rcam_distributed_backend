package tk.bad_rabbit.rcam.distributed_backend.command.state;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;

public interface ICommandState {
  public String getStateExecutableType();
  public void doNetworkStuff(Observer actionObject, ACommand actionSubject);
  public void doRelatedCommandStuff(Observer actionobject, ACommand actionSubject);
  //public void nextState(ACommand actionSubject);
  
  abstract void doNetworkAction(Observer actionObject, ACommand actionSubject);
  abstract void doRelatedCommandAction(Observer actionObject, ACommand actionSubject);
}
