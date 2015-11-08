package tk.bad_rabbit.rcam.distributed_backend.command.state;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;

public class CommandCompletedState implements ICommandState {

  public String getStateExecutableType() {
    return "commandExecutable";
  }
  
  public void doAction(Observer actionObserver, ACommand actionSubject) {
  }

}
