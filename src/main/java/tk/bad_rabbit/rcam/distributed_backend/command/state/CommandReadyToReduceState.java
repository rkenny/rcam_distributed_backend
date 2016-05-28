package tk.bad_rabbit.rcam.distributed_backend.command.state;

import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.CommandReadyToReduceResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ICommandResponseAction;

public class CommandReadyToReduceState extends ACommandState {

  ICommandResponseAction networkResponseAction;
  ICommandResponseAction relatedCommandResponseAction;
  
  public CommandReadyToReduceState() {
    setNetworkResponseAction(new CommandReadyToReduceResponseAction());
  }
  
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
    return null; // not used in the backend. It would be CommandReducedState.
  }

}
