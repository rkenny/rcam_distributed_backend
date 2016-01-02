package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.controller.Controller;

public class AckCommandResponseAction extends ACommandResponseAction {

 @Override
 public void doNetworkStuff(Observer actionObject, ACommand actionSubject) {

  }

 @Override
 public void doRelatedCommandStuff(Observer actionObject, ACommand actionSubject) {
     if( ((ACommand) actionSubject).isType("Ack")) {
       Object ackNumber = ((ACommand) actionSubject).getClientVariable("ackNumber");
       if(ackNumber instanceof Integer) {
         ((Controller) actionObject).ackCommandReceived((Integer) ackNumber);
         ((Controller) actionObject).removeCommand(actionSubject);
       }
     }
 }
 
 public void nextState(ACommand command) {}
 
 
}
