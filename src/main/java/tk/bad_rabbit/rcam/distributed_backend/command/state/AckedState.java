package tk.bad_rabbit.rcam.distributed_backend.command.state;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.controller.Controller;



public class AckedState implements ICommandState {

  public void doAction(Observer actionObserver, ACommand actionSubject) {
    System.out.println("Command " + ((ACommand) actionSubject).getAckNumber() + " did it's ackedState action");
    System.out.println("It was called by " + actionObserver.getClass().getSimpleName());
    if(actionObserver instanceof Controller) {
      ((Controller) actionObserver).runCommand((ACommand) actionSubject);
    }
  }

}
