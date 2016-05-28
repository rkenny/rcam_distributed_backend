package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.Observer;
import java.util.concurrent.Future;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;

abstract public class ACommandResponseAction implements ICommandResponseAction {
  
  abstract public Future doStuff(Observer actionObject, ACommand actionSubject);
  public void nextState(ACommand command) {}
  
}
