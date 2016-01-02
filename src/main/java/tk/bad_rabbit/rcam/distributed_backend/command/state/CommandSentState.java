package tk.bad_rabbit.rcam.distributed_backend.command.state;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;

public class CommandSentState extends ACommandState {

  public String getStateExecutableType() {
    // TODO Auto-generated method stub
    return null;
  }

  public void doNetworkStuff(Observer actionObject, ACommand actionSubject) {
    // TODO Auto-generated method stub
    
  }

  public void doRelatedCommandStuff(Observer actionobject, ACommand actionSubject) {
    // TODO Auto-generated method stub
    
  }
  
  public void nextState(ACommand actionSubject) {
    actionSubject.setState(new AwaitingAckState());
  }

}
