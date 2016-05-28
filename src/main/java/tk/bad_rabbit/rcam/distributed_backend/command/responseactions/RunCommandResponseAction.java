package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.Map;
import java.util.Observer;
import java.util.concurrent.Future;

import org.apache.commons.lang3.concurrent.ConcurrentUtils;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.controller.RunController;

public class RunCommandResponseAction extends ACommandResponseAction {
  @Override
  public Future doStuff(Observer actionObject, ACommand actionSubject) {
    Future<Map.Entry<Integer, Integer>> result;
    System.out.println("RCam Distributed Backend - RunCommandResponseAction doStuff called for " + actionSubject.getCommandName());
    result = ((RunController) actionObject).runCommand(actionSubject);
    while(!result.isDone()) {
      // spin... this could cause a block on the UI.
      //System.out.println("Spinning.");
    }
    System.out.println("RCam Distributed Backend - RunCommandResponseAction says that results.isDone is true.");
    nextState(actionSubject);
    //nextState(actionSubject);
    
    return ConcurrentUtils.constantFuture(true);
  }
  
  public void nextState(ACommand command) {
    System.out.println("RunCommandResponseAction - setting the next state for "+command.getCommandName()+"[" + command.getAckNumber() + "]");
    command.nextState();
  }

}
