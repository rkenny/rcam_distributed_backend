package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.controller.Controller;

public class AckCommandResponseAction implements ICommandResponseAction {

  public void doAction(Object actionObject, ACommand actionSubject) {
    if( ((ACommand) actionSubject).isType("Ack")) {
      ((Controller) actionObject).ackCommandReceived(Integer.parseInt(((ACommand) actionSubject).getClientVariable("ackNumber")));
      ((Controller) actionObject).removeCommand(actionSubject);
      
    }
    
  }

}
