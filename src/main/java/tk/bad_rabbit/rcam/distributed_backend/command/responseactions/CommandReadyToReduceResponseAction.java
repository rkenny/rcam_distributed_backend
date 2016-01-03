package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.client.ClientThread;
import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;

public class CommandReadyToReduceResponseAction extends ACommandResponseAction {
  @Override
  public void doStuff(Observer actionObject, ACommand actionSubject) {
    ((ClientThread) actionObject).sendResult(actionSubject);
    nextState(actionSubject);
  }
  
  public void nextState(ACommand command) {
    System.out.println("Next, the command needs to have a state of WaitingForReduction. Take a break!");
  }
  
  
}
