package tk.bad_rabbit.rcam.distributed_backend.configurationprovider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.AckCommandResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.CancelCommandResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.DefaultCommandResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ICommandResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ResultCommandResponseAction;

public class ConfigurationProvider implements IConfigurationProvider {
  Map<String, JSONObject> commandConfigurations;
  JSONObject serverVariables;
  Map<String, ICommandResponseAction> commandResultResponses;
  
  public ConfigurationProvider() { 
    commandResultResponses = new HashMap<String, ICommandResponseAction>();
    readServerConfiguration();
    readCommandConfigurations();

    JSONObject ackConfiguration = new JSONObject();
    JSONArray ackClientVars = new JSONArray();
    ackClientVars.put("ackNumber");
    ackClientVars.put("command");
    ackConfiguration.put("clientVars", ackClientVars);
    ackConfiguration.put("commandVars", new JSONObject("{ignored: true}"));
    addSystemCommand("Ack", ackConfiguration, new AckCommandResponseAction());
    
    
    JSONObject commandResultConfiguration = new JSONObject();
    JSONArray resultClientVars = new JSONArray();
    resultClientVars.put("ackNumber");
    resultClientVars.put("resultCode");
    commandResultConfiguration.put("clientVars", resultClientVars);
    commandResultConfiguration.put("commandVars", new JSONObject("{ignored: false}"));
    addSystemCommand("CommandResult", commandResultConfiguration, new ResultCommandResponseAction());
    
    JSONObject cancelConfiguration = new JSONObject();
    JSONArray cancelClientVars = new JSONArray();
    cancelClientVars.put("ackNumber");
    cancelClientVars.put("resultCode");
    cancelConfiguration.put("clientVars", cancelClientVars);
    cancelConfiguration.put("commandVars", new JSONObject("{ignored: true}"));
    addSystemCommand("Cancel", cancelConfiguration, new CancelCommandResponseAction());
  }
  
  private void addSystemCommand(String commandType, JSONObject commandConfiguration, ICommandResponseAction commandResponseAction) {
    commandConfigurations.put(commandType, commandConfiguration);
    commandResultResponses.put(commandType, commandResponseAction);
  }
  
  public String getCommandConfigurationPath() {
    return "config/commands";
  }
  
  public JSONObject getCommandConfiguration(String commandType) {
    return this.commandConfigurations.get(commandType);
  }
  
  private void readServerConfiguration() {
    File serverConfigFile = new File("config/server.conf");
    BufferedReader reader;
    try {
      reader = new BufferedReader(new FileReader(serverConfigFile));
      String configFileLine;
      StringBuilder serverConfig = new StringBuilder();
      while((configFileLine = reader.readLine()) != null) {
        serverConfig.append(configFileLine);
      }
      serverVariables = new JSONObject(serverConfig.toString());
    } catch(FileNotFoundException e) {
      System.out.println("File not found. Going with defaults");
      serverVariables = new JSONObject();
      serverVariables.put("port", 12345);
    } catch(IOException e) {
      System.out.println("Error setting server configuration. Going with the defaults.");
      serverVariables = new JSONObject();
      serverVariables.put("port", 12345);
    }
    
  }
  
  private void readCommandConfigurations() {
    commandConfigurations = new HashMap<String, JSONObject>();
    File commandConfigFolder = new File("config/commands");
    for(File commandConfigDirectory : commandConfigFolder.listFiles()) {
      if(commandConfigDirectory.isDirectory()) {
        File commandConfigFile = new File(commandConfigDirectory, "command");
        
        StringBuilder commandArgs = new StringBuilder();
        if(commandConfigFile.isFile()) {
          BufferedReader reader;
          try {
            reader = new BufferedReader(new FileReader(commandConfigFile));
            String configFileLine;
            while((configFileLine = reader.readLine()) != null) {
              commandArgs.append(configFileLine);
            }
            reader.close();
          } catch (FileNotFoundException e) {
            e.printStackTrace();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
        
        commandResultResponses.put(commandConfigDirectory.getName(), new DefaultCommandResponseAction());
      }
    }
  }
  
  public JSONObject getServerVariables() {
    return serverVariables;
  }
  
  public int getServerPort() {
    System.out.println(serverVariables);
    return serverVariables.getInt("port");
  }

  public Map<String, JSONObject> getCommandConfigurations() {
    return commandConfigurations;
  }

  public ICommandResponseAction getCommandResponseAction(String commandType) {
    return commandResultResponses.get(commandType);
  }

}
