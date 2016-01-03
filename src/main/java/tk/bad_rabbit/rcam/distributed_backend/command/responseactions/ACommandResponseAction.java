package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;

abstract public class ACommandResponseAction implements ICommandResponseAction {
  
  //public void doNetworkAction(Observer actionObject, ACommand actionSubject) {
  //  System.out.println("RCam Distributed Backend - " + this.getClass().getSimpleName() + " doNetworkStuff called");
  //  doNetworkStuff(actionObject, actionSubject);
  //}
  
  //public void doRelatedCommandAction(Observer actionObject, ACommand actionSubject) {
  //  System.out.println("RCam Distributed Backend - " + this.getClass().getSimpleName() + " doRelatedCommandStuff called");
  //  doRelatedCommandStuff(actionObject, actionSubject);
  //}
  
  
  abstract public void doStuff(Observer actionObject, ACommand actionSubject);
  //abstract public void doRelatedCommandStuff(Observer actionObject, ACommand actionSubject);
  
}
