package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.Observer;
import java.util.concurrent.Future;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.state.CommandReducedState;
import tk.bad_rabbit.rcam.distributed_backend.controller.RunController;

public class ReductionCompleteResponseAction extends ACommandResponseAction {
  @Override
  public Future doStuff(Observer actionObject, ACommand actionSubject) {
    ACommand relatedCommand = ((RunController) actionObject).getCommand(Integer.parseInt((String) actionSubject.getClientVariable("ackNumber")));
    System.out.println("RCam Distributed Backend - ReductionCompleteResponseAction - Setting Command("+relatedCommand.getCommandName()+"["+relatedCommand.getAckNumber()+"]) to a DoneState");
    //relatedCommand.setState(new DoneState());
    relatedCommand.setState(new CommandReducedState());
    nextState(actionSubject);
    
    return null;
  }
  
  public void nextState(ACommand command) {
    command.nextState();
  }

}
