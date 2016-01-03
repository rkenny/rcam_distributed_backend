package tk.bad_rabbit.rcam.distributed_backend.command.state;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ICommandResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.controller.Controller;

public class CommandReducedState extends ACommandState {

  public String getStateExecutableType() {
    return "commandExecutable";
  }
  
//  
//  public void doNetworkStuff(Observer actionObserver, ACommand actionSubject) {
//  }
//  public void doRelatedCommandStuff(Observer actionObserver, ACommand actionSubject) {
//    if(actionObserver instanceof Controller) {
//      ((Controller) actionObserver).removeCommand((ACommand) actionSubject);
//    }
//  }
  

  ICommandResponseAction networkResponseAction;
  ICommandResponseAction relatedCommandResponseAction;
  
  public ICommandResponseAction getNetworkResponseAction() {
    // TODO Auto-generated method stub
    return networkResponseAction;
  }
  public ICommandResponseAction getRelatedCommandResponseAction() {
    return relatedCommandResponseAction;
  }
  public void setNetworkResponseAction(ICommandResponseAction newNetworkResponseAction) {
    this.networkResponseAction = newNetworkResponseAction;
  }
  public void setRelatedCommandResponseAction(ICommandResponseAction newRelatedCommandResponseAction) {
    this.relatedCommandResponseAction = newRelatedCommandResponseAction;
  }
  
  ICommandResponseAction runCommandResponseAction;
  public ICommandResponseAction getRunCommandResponseAction() { return this.runCommandResponseAction; }
  public void setRunCommandResponseAction(ICommandResponseAction newRunCommandResponseAction) {  this.runCommandResponseAction = newRunCommandResponseAction; }
  

}
