package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.state.AckedState;
import tk.bad_rabbit.rcam.distributed_backend.server.Server;
import tk.bad_rabbit.rcam.distributed_backend.server.ServerThread;

public class DefaultCommandResponseAction implements ICommandResponseAction {
  public void doAction(Object actionObject, ACommand actionSubject) {
      if(actionObject instanceof ServerThread) {
        System.out.println("Now "+actionSubject.getAckNumber() +" it is going to send an ack");
        ((ServerThread) actionObject).sendAck(actionSubject);
      }
    }
}
