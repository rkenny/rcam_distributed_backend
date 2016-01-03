package tk.bad_rabbit.rcam.distributed_backend.command.state;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.client.ClientThread;
import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ICommandResponseAction;

public class AwaitingAckState extends ACommandState {
  public String getStateExecutableType() {
    return "commandExecutable";
  }
  
//  public void doNetworkStuff(Observer actionObserver, ACommand actionSubject) {
//    if(actionObserver instanceof ClientThread) {
//      ((ClientThread) actionObserver).sendAck((ACommand)actionSubject);
//      ((ACommand) actionSubject).setState(new AckedState());
//    }
//  }
//  
//  public void doRelatedCommandStuff(Observer actionObserver, ACommand actionSubject) {}

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

}
