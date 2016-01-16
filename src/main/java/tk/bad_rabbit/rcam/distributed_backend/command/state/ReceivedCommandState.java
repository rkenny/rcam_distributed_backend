package tk.bad_rabbit.rcam.distributed_backend.command.state;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ICommandResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ReductionCompleteResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.SendAckAction;


public class ReceivedCommandState extends ACommandState {

  public String getStateExecutableType() {
    return "commandExecutable";
  }
  
  ICommandResponseAction networkResponseAction;
  ICommandResponseAction relatedCommandResponseAction;
  
  public ReceivedCommandState() {
  }
  
  public void doNetworkAction(Observer actionObserver, ACommand actionSubject) {
    
    if(!(actionSubject.isIgnored())) {
      setNetworkResponseAction(new SendAckAction());
    }
    
    getNetworkResponseAction().doStuff(actionObserver, actionSubject);
  }
  
  public void doRelatedCommandAction(Observer actionObserver, ACommand actionSubject) {
    if(actionSubject.getCommandName().equals("ReductionComplete")) {
      setRelatedCommandResponseAction(new ReductionCompleteResponseAction());
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
  

}
