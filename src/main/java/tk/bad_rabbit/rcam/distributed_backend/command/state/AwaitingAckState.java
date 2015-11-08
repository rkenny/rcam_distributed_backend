package tk.bad_rabbit.rcam.distributed_backend.command.state;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.server.ServerThread;

public class AwaitingAckState implements ICommandState {
  public String getStateExecutableType() {
    return "commandExecutable";
  }
  
  public void doAction(Observer actionObserver, ACommand actionSubject) {
    if(actionObserver instanceof ServerThread) {
      ((ServerThread) actionObserver).sendAck((ACommand)actionSubject);
      ((ACommand) actionSubject).setState(new AckedState());
    }
  }

}
