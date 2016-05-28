package tk.bad_rabbit.rcam.distributed_backend.command.state;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.CancelRelatedCommandResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ICommandResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ReductionCompleteResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.SendAckAction;


public class ReceivedCommandState extends ACommandState {
  
  ICommandResponseAction networkResponseAction;
  ICommandResponseAction relatedCommandResponseAction;
  
  public ReceivedCommandState() {
  }
  
  public void doNetworkAction(Observer actionObserver, ACommand actionSubject) {
    System.out.println("ReceivedCommandState doNetworkAction called on " + actionSubject.getCommandName());
    if(!(actionSubject.isIgnored())) {
      setNetworkResponseAction(new SendAckAction());
    }
    
    getNetworkResponseAction().doStuff(actionObserver, actionSubject);

    
  }
  
  public void doRelatedCommandAction(Observer actionObserver, ACommand actionSubject) {
    if(actionSubject.getCommandName().equals("ReductionComplete")) {
      setRelatedCommandResponseAction(new ReductionCompleteResponseAction());
    }
    
    if(actionSubject.getCommandName().equals("Cancel")) {
      setRelatedCommandResponseAction(new CancelRelatedCommandResponseAction());
    }
    
    getRelatedCommandResponseAction().doStuff(actionObserver, actionSubject);
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
    return new DoneState(); // look into using reflection.
  }

}
