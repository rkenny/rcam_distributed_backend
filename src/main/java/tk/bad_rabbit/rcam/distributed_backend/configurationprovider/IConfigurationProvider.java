package tk.bad_rabbit.rcam.distributed_backend.configurationprovider;

import java.util.List;
import java.util.Map;

import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ICommandResponseAction;

public interface IConfigurationProvider {
  public int getServerPort();

  public Map<String, List<String>> getCommandConfigurations();
  public Map<String, Map<String, String>> getCommandVariables();
  public Map<String, String> getServerVariables();
  public ICommandResponseAction getCommandResponseAction(String commandType);
}
