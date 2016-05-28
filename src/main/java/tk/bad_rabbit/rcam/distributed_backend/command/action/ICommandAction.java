package tk.bad_rabbit.rcam.distributed_backend.command.action;

import java.nio.CharBuffer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.IConfigurationProvider;

public interface ICommandAction {

  void setCommandDetails(ACommand aCommand);
  public CharBuffer asCharBuffer();
  public void takeNecessaryInfo(IConfigurationProvider configurationProvider);
  public ICommandAction nextAction();
}
