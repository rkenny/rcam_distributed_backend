package tk.bad_rabbit.rcam.distributed_backend.command.state;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.client.ClientThread;
import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;

public class AwaitingAckState extends ACommandState {
  public String getStateExecutableType() {
    return "commandExecutable";
  }
  
  public void doNetworkStuff(Observer actionObserver, ACommand actionSubject) {
    if(actionObserver instanceof ClientThread) {
      ((ClientThread) actionObserver).sendAck((ACommand)actionSubject);
      ((ACommand) actionSubject).setState(new AckedState());
    }
  }
  
  public void doRelatedCommandStuff(Observer actionObserver, ACommand actionSubject) {}

}
