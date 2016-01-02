package tk.bad_rabbit.rcam.distributed_backend.command.state;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.controller.Controller;

public class CommandReducedState extends ACommandState {

  public String getStateExecutableType() {
    return "commandExecutable";
  }
  
  
  public void doNetworkStuff(Observer actionObserver, ACommand actionSubject) {
  }
  public void doRelatedCommandStuff(Observer actionObserver, ACommand actionSubject) {
    if(actionObserver instanceof Controller) {
      ((Controller) actionObserver).removeCommand((ACommand) actionSubject);
    }
  }

}
