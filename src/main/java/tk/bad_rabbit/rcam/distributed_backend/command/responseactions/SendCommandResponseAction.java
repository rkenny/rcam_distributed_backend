package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.client.ClientThread;
import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.state.CommandSentState;

public class SendCommandResponseAction extends ACommandResponseAction{
  public void nextState(ACommand command) {
    command.setState(new CommandSentState());
  }

  @Override
  public void doStuff(Observer actionObject, ACommand actionSubject) {
    ((ClientThread) actionObject).send(actionSubject);
    nextState(actionSubject);
  }

}
