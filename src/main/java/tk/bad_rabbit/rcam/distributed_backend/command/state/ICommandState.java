package tk.bad_rabbit.rcam.distributed_backend.command.state;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ICommandResponseAction;

public interface ICommandState {
  public String getStateExecutableType();
  //public void nextState(ACommand actionSubject);
  
  abstract void doNetworkAction(Observer actionObject, ACommand actionSubject);
  abstract void doRelatedCommandAction(Observer actionObject, ACommand actionSubject);
  
  ICommandResponseAction getNetworkResponseAction();
  ICommandResponseAction getRelatedCommandResponseAction();
  
  void setNetworkResponseAction(ICommandResponseAction newNetworkResponseAction);
  void setRelatedCommandResponseAction(ICommandResponseAction newRelatedCommandResponseAction);
  
}
