package tk.bad_rabbit.rcam.distributed_backend.command.state;

import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ICommandResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.RunCommandResponseAction;



public class AckSentState extends ACommandState {
  public String getStateExecutableType() {
    return "commandExecutable";
  }
  
//  public void doNetworkStuff(Observer actionObserver, ACommand actionSubject) {
//    System.out.println("RCam Distributed Backend - AckedState - doNetworkStuff for " + actionSubject.getCommandName() + "[" + actionSubject.getAckNumber() + "]");
//  }
//  
//  public void doRelatedCommandStuff(Observer actionObserver, ACommand actionSubject) {
//    System.out.println("RCam Distributed Backend - AckedState - doRelatedCommandStuff for " + actionSubject.getCommandName() + "[" + actionSubject.getAckNumber() + "]");
//    if(actionObserver instanceof Controller) {
//      ((Controller) actionObserver).runCommand((ACommand) actionSubject);
//    }
//  }
//  
//  public void nextState(ACommand actionSubject) {
//    //actionSubject.setState(new ReadyToReduceState());
//  }
  

  ICommandResponseAction networkResponseAction;
  ICommandResponseAction relatedCommandResponseAction;
  
  public AckSentState() {
    this.relatedCommandResponseAction = new RunCommandResponseAction();
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

}
