package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.controller.Controller;

public class ResultCommandResponseAction extends ACommandResponseAction {

  public void doRelatedCommandStuff(Observer actionObject, ACommand actionSubject) {
  }
  
  public void doNetworkStuff(Observer actionObject, ACommand actionSubject) {
    if( ((ACommand) actionSubject).isType("CommandResult")) {
      ((Controller) actionObject).removeCommand(actionSubject);
    }
  }

  public void nextState(ACommand command) {}
  
}
