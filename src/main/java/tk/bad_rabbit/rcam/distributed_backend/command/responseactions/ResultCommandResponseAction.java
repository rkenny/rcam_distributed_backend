package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.Observer;
import java.util.concurrent.Future;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;

public class ResultCommandResponseAction extends ACommandResponseAction {

  @Override
  public Future doStuff(Observer actionObject, ACommand actionSubject) {
    return null;
  }
  
  public void nextState(ACommand command) {}

  
}
