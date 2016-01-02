package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.controller.Controller;

public class CancelCommandResponseAction extends ACommandResponseAction {

  
  
  public void doNetworkStuff(Observer actionObject, ACommand actionSubject) {
    if(actionObject instanceof Controller) {
      ((Controller) actionObject).cancelCommandReceived(Integer.parseInt(actionSubject.getClientVariable("ackNumber").toString()));
    }
  }
  
  public void doRelatedCommandStuff(Observer actionObject, ACommand actionSubject) {
    if(actionObject instanceof Controller) {
      ((Controller) actionObject).cancelCommandReceived(Integer.parseInt(actionSubject.getClientVariable("ackNumber").toString()));
    }
  }

  public void nextState(ACommand command) {
    // TODO Auto-generated method stub
    
  }

}
