package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.Observer;
import java.util.concurrent.Future;

import tk.bad_rabbit.rcam.distributed_backend.client.ClientThread;
import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;

public class SendCommandResponseAction extends ACommandResponseAction{
  public void nextState(ACommand command) {
    command.nextState();
  }

  @Override
  public Future doStuff(Observer actionObject, ACommand actionSubject) {
    ((ClientThread) actionObject).send(actionSubject);
    nextState(actionSubject);
    
    return null;
  }

}
