package tk.bad_rabbit.rcam.distributed_backend.configurationprovider;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ICommandResponseAction;

public interface IConfigurationProvider {
  public int getServerPort();

  public Map<String, JSONObject> getCommandConfigurations();
  public JSONObject getCommandConfiguration(String commandType);
  public String getCommandConfigurationPath();
  
  public JSONObject getServerVariables();
  public ICommandResponseAction getCommandResponseAction(String commandType);
  
}
