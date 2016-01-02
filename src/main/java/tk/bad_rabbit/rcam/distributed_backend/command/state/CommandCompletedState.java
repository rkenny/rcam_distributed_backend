package tk.bad_rabbit.rcam.distributed_backend.command.state;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;

public class CommandCompletedState extends ACommandState {

  public String getStateExecutableType() {
    return "commandExecutable";
  }
  
  public void doNetworkStuff(Observer actionObserver, ACommand actionSubject) {
  }
  public void doRelatedCommandStuff(Observer actionObserver, ACommand actionSubject) {}

}
