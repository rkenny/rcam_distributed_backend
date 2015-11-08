package tk.bad_rabbit.rcam.distributed_backend.command.state;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;

public interface ICommandState {
  public String getStateExecutableType();
  public void doAction(Observer actionObject, ACommand actionSubject);
}
