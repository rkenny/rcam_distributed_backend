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

import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.AckCommandResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.DefaultCommandResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ICommandResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ResultCommandResponseAction;

public class ConfigurationProvider implements IConfigurationProvider {
  Map<String, List<String>> commandConfigurations;
  Map<String, Map<String, String>> commandVariables;
  Map<String, String> serverVariables;
  Map<String, ICommandResponseAction> commandResultResponses;
  
  public ConfigurationProvider() {
    serverVariables = new HashMap<String, String>();
    commandResultResponses = new HashMap<String, ICommandResponseAction>();
    readServerConfiguration();
    readCommandConfigurations();
    

    addSystemCommand("Ack", "{command:&command,ackNumber:&ackNumber}", "true", new AckCommandResponseAction());
    addSystemCommand("CommandResult", "{ackNumber:&ackNumber,resultCode:&resultCode}", "false", new ResultCommandResponseAction());
    
  }
  
  private void addSystemCommand(String commandType, String commandString, String isIgnored, ICommandResponseAction commandResultResponseAction) {
    List<String> successCommand = new ArrayList<String>();
    successCommand.add(commandString);
    commandConfigurations.put(commandType, successCommand);
    Map<String, String> successCommandVariables = new HashMap<String, String>();
    successCommandVariables.put("ignored", isIgnored);
    commandVariables.put(commandType, successCommandVariables);
    commandResultResponses.put(commandType, commandResultResponseAction);
  }
  
  private void readServerConfiguration() {
    //serverPort = 12345;
    File serverConfigFile = new File("config/server.conf");
    BufferedReader reader;
    try {
      reader = new BufferedReader(new FileReader(serverConfigFile));
      String configFileLine;
      while((configFileLine = reader.readLine()) != null) {
        parseServerConfigLine(configFileLine);
      }
    } catch(FileNotFoundException e) {
      System.out.println("File not found. Going with defaults");
      serverVariables.put("port", "12345");
    } catch(IOException e) {
      System.out.println("Error setting server configuration. Going with the defaults.");
      serverVariables.put("port", "12345");
    }
    
  }
  
  private void parseServerConfigLine(String configFileLine) {
    String[] variableAndValue;
    if(configFileLine.indexOf("=") > 0) {
      variableAndValue = configFileLine.split("=");
      serverVariables.put(variableAndValue[0], variableAndValue[1]);
    }
    
    
  }
  
  private void readCommandConfigurations() {
    commandConfigurations = new HashMap<String, List<String>>();
    commandVariables = new HashMap<String, Map<String, String>>();
    File commandConfigFolder = new File("config/commands");
    for(File commandConfigDirectory : commandConfigFolder.listFiles()) {
      if(commandConfigDirectory.isDirectory()) {
        File commandConfigFile = new File(commandConfigDirectory, "command");
        
        List<String> commandArgs = new ArrayList<String>();
        if(commandConfigFile.isFile()) {
          BufferedReader reader;
          try {
            reader = new BufferedReader(new FileReader(commandConfigFile));
            String configFileLine;
            while((configFileLine = reader.readLine()) != null) {
              commandArgs.add(configFileLine);
            }
            reader.close();
          } catch (FileNotFoundException e) {
            e.printStackTrace();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
        
        File commandVariableFile = new File(commandConfigDirectory, "vars");
        Map<String, String> commandVars = new HashMap<String, String>();
        if(commandVariableFile.isFile()) {
          BufferedReader reader;
          try {
            reader = new BufferedReader(new FileReader(commandVariableFile));
            String configFileLine;
            while((configFileLine = reader.readLine()) != null) {
              if(configFileLine.indexOf("=") > 0) {
                String[] variableAndValue = configFileLine.split("=");
                commandVars.put(variableAndValue[0], variableAndValue[1]);
              }
            }
            reader.close();
          } catch (FileNotFoundException e) {
            e.printStackTrace();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
        
        
        
        commandConfigurations.put(commandConfigDirectory.getName(), commandArgs);
        commandVariables.put(commandConfigDirectory.getName(), commandVars);
        commandResultResponses.put(commandConfigDirectory.getName(), new DefaultCommandResponseAction());
      }
    }
  }
  
  public Map<String, String> getServerVariables() {
    return serverVariables;
  }
  
  public int getServerPort() {
    return Integer.parseInt(serverVariables.get("port"));
  }

  public Map<String, List<String>> getCommandConfigurations() {
    return commandConfigurations;
  }
  
  public Map<String, Map<String, String>> getCommandVariables() {
    return commandVariables;
  }

  public ICommandResponseAction getCommandResponseAction(String commandType) {
    return commandResultResponses.get(commandType);
  }

}
