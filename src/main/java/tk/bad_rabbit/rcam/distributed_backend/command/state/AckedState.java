package tk.bad_rabbit.rcam.distributed_backend.command.state;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.controller.Controller;



public class AckedState implements ICommandState {

  public void doAction(Observer actionObserver, ACommand actionSubject) {
    if(actionObserver instanceof Controller) {
      ((Controller) actionObserver).runCommand((ACommand) actionSubject);
    }
  }

}
