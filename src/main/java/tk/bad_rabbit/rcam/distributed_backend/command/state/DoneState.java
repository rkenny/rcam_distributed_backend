package tk.bad_rabbit.rcam.distributed_backend.command.state;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.server.ServerThread;


public class DoneState implements ICommandState {

  public String getStateExecutableType() {
    return "commandExecutable";
  }
  
  public void doAction(Observer actionObserver, ACommand command) {
    System.out.println("Command is in the doneState");
    if(actionObserver instanceof ServerThread) {
      ((ServerThread) actionObserver).sendResult(command);
    }
  }

}
