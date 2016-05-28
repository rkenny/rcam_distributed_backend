package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.Observer;
import java.util.concurrent.Future;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.controller.RunController;

public class CancelCommandResponseAction extends ACommandResponseAction {

  @Override
  public Future doStuff(Observer actionObject, ACommand actionSubject) {
    ((RunController) actionObject).runCommand(actionSubject);
    nextState(actionSubject);
    
    return null;
  }
  
  public void nextState(ACommand command) {
    command.nextState();
  }

  
}
