package tk.bad_rabbit.rcam.distributed_backend.command.state;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.controller.Controller;


public class ErrorCommandState implements ICommandState {

  public String getStateExecutableType() {
    return "cancelExecutable";
  }
  
  public void doAction(Observer actionObject, ACommand actionSubject) {
    if(actionObject instanceof Controller) {
      ((Controller) actionObject).cancelCommand(actionSubject);
    }

  }

}
