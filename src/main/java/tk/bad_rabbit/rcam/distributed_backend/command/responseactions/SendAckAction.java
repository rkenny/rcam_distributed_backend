package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.Observer;
import java.util.concurrent.Future;

import org.apache.commons.lang3.concurrent.ConcurrentUtils;

import tk.bad_rabbit.rcam.distributed_backend.client.ClientThread;
import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.state.AckSentState;

public class SendAckAction extends ACommandResponseAction {

  public void nextState(ACommand command) {
    command.setState(new AckSentState());
  }

  @Override
  public Future doStuff(Observer actionObject, ACommand actionSubject) {
    //((ClientThread) actionObject).sendAck(actionSubject);
    nextState(actionSubject);
    
    return ConcurrentUtils.constantFuture(true);
  }

}
