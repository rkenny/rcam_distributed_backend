package tk.bad_rabbit.rcam.distributed_backend.command.state;

import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ICommandResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.RunCleanupResponseAction;

public class CommandReducedState extends ACommandState {


  ICommandResponseAction networkResponseAction;
  ICommandResponseAction relatedCommandResponseAction;
  
  public CommandReducedState() {
    System.out.println("Command Reduced State created.");
    this.setRunCommandResponseAction(new RunCleanupResponseAction());
  }
  
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
  
  public ACommandState getNextState() {
    return new DoneState();
  }
  
}
