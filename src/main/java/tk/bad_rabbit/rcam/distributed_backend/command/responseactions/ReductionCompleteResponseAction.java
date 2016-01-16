package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.state.DoneState;
import tk.bad_rabbit.rcam.distributed_backend.controller.Controller;

public class ReductionCompleteResponseAction extends ACommandResponseAction {
  @Override
  public void doStuff(Observer actionObject, ACommand actionSubject) {
    ACommand relatedCommand = ((Controller) actionObject).getCommand(Integer.parseInt((String) actionSubject.getClientVariable("ackNumber")));
    
    relatedCommand.setState(new DoneState());
    nextState(actionSubject);
  }
  
  public void nextState(ACommand command) {
    command.setState(new DoneState());
  }

}
