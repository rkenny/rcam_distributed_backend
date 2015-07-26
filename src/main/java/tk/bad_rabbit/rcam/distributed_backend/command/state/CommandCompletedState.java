package tk.bad_rabbit.rcam.distributed_backend.command.state;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;

public class CommandCompletedState implements ICommandState {

  public void doAction(Observer actionObserver, ACommand actionSubject) {
    System.out.println("Commands on the backend are not reduced");
    //if(actionObject instanceof Controller) {
    //  ((Controller) actionObject).readyToReduce((ACommand) actionSubject);
    // }
  }

}
