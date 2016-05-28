package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.Observer;
import java.util.concurrent.Future;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;



public interface ICommandResponseAction {
  //public void doNetworkAction(Observer actionObject, ACommand actionSubject);
  //public void doRelatedCommandAction(Observer actionObject, ACommand actionSubject);
  
  //abstract public void doNetworkStuff(Observer actionObject, ACommand actionSubject);
  //abstract public void doRelatedCommandStuff(Observer actionObject, ACommand actionSubject);

  public Future doStuff(Observer actionObject, ACommand actionSubject);
  abstract public void nextState(ACommand command);
  
  
}
