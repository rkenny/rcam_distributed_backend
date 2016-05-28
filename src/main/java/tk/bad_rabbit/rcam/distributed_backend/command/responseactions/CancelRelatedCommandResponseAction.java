package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.Observer;
import java.util.concurrent.Future;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.state.CancellingState;
import tk.bad_rabbit.rcam.distributed_backend.controller.RunController;

public class CancelRelatedCommandResponseAction extends ACommandResponseAction {
  @Override
  public Future doStuff(Observer actionObject, ACommand actionSubject) {
    ACommand relatedCommand = ((RunController) actionObject).getCommand(Integer.parseInt((String) actionSubject.getClientVariable("ackNumber")));
    if(relatedCommand != null) {
      relatedCommand.setState(new CancellingState());
    }
    
    return null;
    //nextState(actionSubject);
  }
  
  public void nextState( ACommand command) {
    command.nextState();
  }

  

}
