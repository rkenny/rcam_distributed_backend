package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.state.ErrorCommandState;
import tk.bad_rabbit.rcam.distributed_backend.controller.Controller;

public class CancelCommandResponseAction implements ICommandResponseAction {

  public void doAction(Object actionObject, ACommand actionSubject) {
    if(actionObject instanceof Controller) {
      ((Controller) actionObject).cancelCommandReceived(Integer.parseInt(actionSubject.getClientVariable("ackNumber").toString()));
    }

  }

}
