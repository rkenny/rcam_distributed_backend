package tk.bad_rabbit.rcam.distributed_backend.command.state;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ICommandResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.controller.RunController;



public class AckedState extends ACommandState {
 
  ICommandResponseAction networkResponseAction;
  ICommandResponseAction relatedCommandResponseAction;
  
  public ICommandResponseAction getNetworkResponseAction() {
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
  
  public ACommandState getNextState() {
    return null; // This isn't called in the backend yet. It would be ready to run.
  }
  
}
