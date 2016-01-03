package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.client.ClientThread;
import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.state.AckSentState;
import tk.bad_rabbit.rcam.distributed_backend.command.state.AckedState;

public class SendAckAction extends ACommandResponseAction {

  public void nextState(ACommand command) {
    command.setState(new AckSentState());
  }

  @Override
  public void doStuff(Observer actionObject, ACommand actionSubject) {
    ((ClientThread) actionObject).sendAck(actionSubject);
    nextState(actionSubject);
  }

}
