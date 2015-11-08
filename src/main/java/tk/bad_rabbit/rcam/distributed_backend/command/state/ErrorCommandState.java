package tk.bad_rabbit.rcam.distributed_backend.command.state;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.controller.Controller;


public class ErrorCommandState implements ICommandState {

  public String getStateExecutableType() {
    System.out.println("ErrorCommandState.getStateExecutableType() returning cancelExecutable.");
    return "cancelExecutable";
  }
  
  public void doAction(Observer actionObject, ACommand actionSubject) {
    System.out.println("Did errorcommandstate call its doAction?");
    if(actionObject instanceof Controller) {
      //actionSubject.setState(new ErrorCommandState());
      ((Controller) actionObject).cancelCommand(actionSubject);
    }

  }

}
