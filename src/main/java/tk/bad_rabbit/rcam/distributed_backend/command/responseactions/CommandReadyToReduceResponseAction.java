package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.Observer;
import java.util.concurrent.Future;

import tk.bad_rabbit.rcam.distributed_backend.client.ClientThread;
import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.state.WaitingForReductionState;

public class CommandReadyToReduceResponseAction extends ACommandResponseAction {
  @Override
  public Future doStuff(Observer actionObject, ACommand actionSubject) {
    //((ClientThread) actionObject).sendResult(actionSubject);
    nextState(actionSubject);
    
    return null;
  }
  
  public void nextState(ACommand command) {
    System.out.println("Next, the command needs to have a state of WaitingForReduction. Take a break!");
    command.setState(new WaitingForReductionState());
  }
  
  
}
