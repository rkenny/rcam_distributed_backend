package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.state.RunningState;
import tk.bad_rabbit.rcam.distributed_backend.controller.Controller;

public class RunCommandResponseAction extends ACommandResponseAction {
  @Override
  public void doStuff(Observer actionObject, ACommand actionSubject) {
    System.out.println("RCam Distributed Backend - RunCommandResponseAction - Will ask the Controller to run Command("+actionSubject.getCommandName()+"["+actionSubject.getAckNumber()+"]");
    ((Controller) actionObject).runCommand(actionSubject);
    nextState(actionSubject);
  }
  
  public void nextState(ACommand command) {
    command.setState(new RunningState());
  }

}
