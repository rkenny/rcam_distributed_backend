package tk.bad_rabbit.rcam.distributed_backend.command.state;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.controller.Controller;


public class ErrorCommandState extends ACommandState {

  public String getStateExecutableType() {
    return "cancelExecutable";
  }
  
  public void doNetworkStuff(Observer actionObserver, ACommand actionSubject) {
  }
  public void doRelatedCommandStuff(Observer actionObserver, ACommand actionSubject) { 
    if(actionObserver instanceof Controller) {
      ((Controller) actionObserver).cancelCommand(actionSubject);
    }

  }

}
