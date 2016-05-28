package tk.bad_rabbit.rcam.distributed_backend.command.state;

import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ICommandResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.RunCommandResponseAction;



public class AckSentState extends ACommandState {
  
  
  ICommandResponseAction networkResponseAction;
  ICommandResponseAction relatedCommandResponseAction;
  
  public AckSentState() {
    System.out.println("Ack Sent State created. The double-run defect is caused by the run command response action changing states before the run runController finishes the actions for ack sentstate");
    setRunCommandResponseAction(new RunCommandResponseAction());
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
  
  public void setRunCommandResponseAction(ICommandResponseAction newRunCommandResponseAction) {  
    this.runCommandResponseAction = newRunCommandResponseAction; 
  }
  
  public ACommandState getNextState() {
    return new CommandReadyToReduceState();
  }
  
}
