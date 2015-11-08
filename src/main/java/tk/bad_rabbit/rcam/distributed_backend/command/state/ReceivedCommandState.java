package tk.bad_rabbit.rcam.distributed_backend.command.state;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.server.ServerThread;


public class ReceivedCommandState implements ICommandState {

  public String getStateExecutableType() {
    return "commandExecutable";
  }
  
  public void doAction(Observer actionObserver, ACommand command) {
    // shit. This could be the root of a few unseen defects.
      (command).performCommandResponseAction(actionObserver);
    //}
  }

}
