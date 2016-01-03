package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.client.ClientThread;
import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.state.AckedState;

public class DefaultCommandResponseAction extends ACommandResponseAction {
  @Override
  public void doStuff(Observer actionObject, ACommand actionSubject) {}
  
  public void nextState(ACommand command) {}

}


