package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.controller.Controller;

public class AckCommandResponseAction implements ICommandResponseAction {

  public void doAction(Object actionObject, ACommand actionSubject) {
    if( ((ACommand) actionSubject).isType("Ack")) {
      Object ackNumber = ((ACommand) actionSubject).getClientVariable("ackNumber");
      if(ackNumber instanceof Integer) {
        ((Controller) actionObject).ackCommandReceived((Integer) ackNumber);
        ((Controller) actionObject).removeCommand(actionSubject);
      }
      
    }
    
  }

}
