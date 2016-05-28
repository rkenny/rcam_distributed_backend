package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.Observer;
import java.util.concurrent.Future;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.controller.RunController;

public class RunCleanupResponseAction extends ACommandResponseAction {

  @Override
  public Future doStuff(Observer actionObject, ACommand actionSubject) {
    System.out.println("RCam Distributed Backend - This will run the cleanup.");
    ((RunController) actionObject).runCommand(actionSubject);
    
    return null;
  }

}
