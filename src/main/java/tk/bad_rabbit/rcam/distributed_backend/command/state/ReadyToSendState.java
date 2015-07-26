package tk.bad_rabbit.rcam.distributed_backend.command.state;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.server.ServerThread;

public class ReadyToSendState implements ICommandState {
  
  public void doAction(Observer actionObserver, ACommand actionSubject) {
    if(actionObserver instanceof ServerThread ) {
      ((ServerThread) actionObserver).send((ACommand) actionSubject);
    }
  }

}
