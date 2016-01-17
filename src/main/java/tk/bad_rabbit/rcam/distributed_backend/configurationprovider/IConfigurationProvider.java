package tk.bad_rabbit.rcam.distributed_backend.configurationprovider;

import java.util.Map;

import org.json.JSONObject;

import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ACommandResponseAction;

public interface IConfigurationProvider {

  public Map<String, JSONObject> getCommandConfigurations();
  public JSONObject getCommandConfiguration(String commandType);
  public String getCommandConfigurationPath();
  
  public JSONObject getServerVariables();
  
  public Object getServerVariable(String key);
  public void setServerVariable(String key, Object value);
  public ACommandResponseAction getCommandResponseAction(String commandType);
  
}
