package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;



public interface ICommandResponseAction {
  public void doNetworkAction(Observer actionObject, ACommand actionSubject);
  public void doRelatedCommandAction(Observer actionObject, ACommand actionSubject);
  
  abstract public void nextState(ACommand command);
  
  abstract public void doNetworkStuff(Observer actionObject, ACommand actionSubject);
  abstract public void doRelatedCommandStuff(Observer actionObject, ACommand actionSubject);

  
}
