package tk.bad_rabbit.rcam.distributed_backend.command.state;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.server.ServerThread;


public class ReceivedCommandState implements ICommandState {

  public void doAction(Observer actionObserver, ACommand command) {
    System.out.println("Command " + command.getAckNumber() + " Did it's received command action");
    System.out.println("It was called by " + actionObserver.getClass().getSimpleName());
    if(actionObserver instanceof ServerThread) {
      System.out.println("to a server thread, so performCommandResponseAction will be called");
      (command).performCommandResponseAction(actionObserver);
    }
  }

}
