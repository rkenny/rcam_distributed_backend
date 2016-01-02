package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.client.ClientThread;
import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.state.AckedState;

public class DefaultCommandResponseAction extends ACommandResponseAction {
  
  public void doRelatedCommandStuff(Observer actionObject, ACommand actionSubject) {
  }
  
  public void doNetworkStuff(Observer actionObject, ACommand actionSubject) {
      if(actionObject instanceof ClientThread) {
        ((ClientThread) actionObject).sendAck(actionSubject);
        nextState(actionSubject);
      }
    }
  
  
  public void nextState(ACommand command) {
    command.setState(new AckedState());
  }
}


