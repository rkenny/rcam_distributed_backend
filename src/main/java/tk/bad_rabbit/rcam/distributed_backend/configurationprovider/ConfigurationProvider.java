package tk.bad_rabbit.rcam.distributed_backend.configurationprovider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.AckCommandResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.DefaultCommandResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ICommandResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ResultCommandResponseAction;

public class ConfigurationProvider implements IConfigurationProvider {
  Map<String, JSONObject> commandConfigurations;
  Map<String, JSONObject> commandVariables;
  JSONObject serverVariables;
  Map<String, ICommandResponseAction> commandResultResponses;
  
  public ConfigurationProvider() {
//    serverVariables = 
    commandResultResponses = new HashMap<String, ICommandResponseAction>();
    readServerConfiguration();
    readCommandConfigurations();

    //addSystemCommand("Ack", "{command:&command,ackNumber:&ackNumber}", "true", new AckCommandResponseAction());
    //addSystemCommand("CommandResult", "{ackNumber:&ackNumber,resultCode:&resultCode}", "false", new ResultCommandResponseAction());
    JSONObject ackConfiguration = new JSONObject();

    ackConfiguration.put("clientVars", new String[]{"ackNumber", "command"});
    ackConfiguration.put("commandVars", new JSONObject("{ignored: true}"));
    addSystemCommand("Ack", ackConfiguration, new AckCommandResponseAction());
    
    JSONObject commandResultConfiguration = new JSONObject();
    commandResultConfiguration.put("clientVars", new String[]{"ackNumber", "resultCode"});
    commandResultConfiguration.put("commandVars", new JSONObject("{ignored: false}"));
    addSystemCommand("CommandResult", commandResultConfiguration, new ResultCommandResponseAction());
  }
  
  private void addSystemCommand(String commandType, JSONObject commandConfiguration, ICommandResponseAction commandResponseAction) {
    commandConfigurations.put(commandType, commandConfiguration);
    System.out.println("Is clientVars an array here? " + commandConfiguration.get("clientVars").getClass().getSimpleName());
  }
  
  private void readServerConfiguration() {
    //serverPort = 12345;
    File serverConfigFile = new File("config/server.conf");
    BufferedReader reader;
    try {
      reader = new BufferedReader(new FileReader(serverConfigFile));
      String configFileLine;
      StringBuilder serverConfig = new StringBuilder();
      while((configFileLine = reader.readLine()) != null) {
        //parseServerConfigLine(configFileLine);
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
    commandVariables = new HashMap<String, JSONObject>();
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
        
        File commandVariableFile = new File(commandConfigDirectory, "vars");
        StringBuilder configFile = new StringBuilder();
        if(commandVariableFile.isFile()) {
          BufferedReader reader;
          try {
            reader = new BufferedReader(new FileReader(commandVariableFile));
            String configFileLine;
            while((configFileLine = reader.readLine()) != null) {
              configFile.append(configFileLine);
            }
            reader.close();
          } catch (FileNotFoundException e) {
            e.printStackTrace();
          } catch (IOException e) {
            e.printStackTrace();
          }
          commandVariables.put(commandConfigDirectory.getName(), new JSONObject(configFile.toString()));
        }
        
        
        
        commandConfigurations.put(commandConfigDirectory.getName(), new JSONObject(commandArgs.toString()));
        
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
  
  public Map<String, JSONObject> getCommandVariables() {
    return commandVariables;
  }

  public ICommandResponseAction getCommandResponseAction(String commandType) {
    return commandResultResponses.get(commandType);
  }

}
