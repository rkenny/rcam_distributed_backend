package tk.bad_rabbit.rcam.distributed_backend.commandfactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.CharBuffer;
import java.util.Map;
import java.util.Random;

import org.json.JSONObject;

import tk.bad_rabbit.rcam.distributed_backend.client.ClientThread;
import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.Command;
import tk.bad_rabbit.rcam.distributed_backend.commandcontroller.CommandController;
import tk.bad_rabbit.rcam.distributed_backend.commandcoordinator.CommandCoordinator;
import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.IConfigurationProvider;
import tk.bad_rabbit.rcam.distributed_backend.controller.RunController;

public class CommandFactory implements ICommandFactory {
  String commandConfigurationPath;
  Map<String, JSONObject> commandConfigurations;
  
  JSONObject serverVariables;
  Random rand;
  
  IConfigurationProvider configurationProvider;
  CommandCoordinator commandCoordinator;
  CommandController commandController;
  
  public CommandFactory(ClientThread clientThread, RunController runController, CommandController commandController, IConfigurationProvider configurationProvider) {
    this(configurationProvider);
    this.commandCoordinator = new CommandCoordinator(clientThread, runController, commandController, configurationProvider);
    this.commandController = commandController;
  }
  
  public CommandFactory(IConfigurationProvider configurationProvider) {
    this.commandConfigurationPath = configurationProvider.getCommandConfigurationPath();
    this.commandConfigurations = configurationProvider.getCommandConfigurations();
    this.serverVariables = configurationProvider.getServerVariables();
    rand = new Random();
    this.configurationProvider = configurationProvider;
    
  }
  
  
  public JSONObject createCommandConfiguration(String commandType) {
    StringBuilder commandArgs = new StringBuilder();
    File commandConfigFolder = new File("./config/commands/" + commandType);
    
    if(commandConfigFolder.isDirectory()) {
      File commandConfigFile = new File(commandConfigFolder, "command");
      
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
    } else if(configurationProvider.getCommandConfiguration(commandType) != null) {
      return configurationProvider.getCommandConfiguration(commandType);
    } else {
      commandArgs.append("{}");
    }

    return new JSONObject(commandArgs.toString());
  }
  
  public ACommand createCommand(String commandType, Integer ackNumber, JSONObject details) {
    ACommand command = null;
    command = new Command(commandType, ackNumber, createCommandConfiguration(commandType), details, serverVariables);
    System.out.println("RCam Distirbuted Backend - CommandFactory - Creating Command("+commandType+"["+ackNumber+"])");
    command.addObserver(commandCoordinator);
    commandController.addCommand(command);
    return command;
  }
  
  //public ACommand createResultCommand(ACommand command) {
  //  return createCommand("CommandResult{ackNumber:"+command.getAckNumber()+",resultCode:"+command.getReturnCode()+"}");
  //}
    
  //public ACommand createAckCommand(ACommand command) {
  //  return createCommand("Ack{command:" + command.getCommandName() + ",ackNumber:"+command.getAckNumber()+"}");
  //}
  
  public ACommand createCommand(CharBuffer commandBuffer) {
    return createCommand(commandBuffer.toString());
  }
  
  public ACommand createCommand(String commandString) {
    if(commandString == null || commandString.length() == 0) {
      return null;
    }
    
    ACommand command = null;
    
    JSONObject commandObject = new JSONObject(commandString);
    
    String commandType;
    Integer commandAckNumber;
    
    commandType = commandObject.getString("commandName");
    commandAckNumber = commandObject.getInt("ackNumber");
    JSONObject commandDetails = null; 
    if(commandObject.has("details")) {
      commandDetails = commandObject.getJSONObject("details");
    }
    
    

    return createCommand(commandType, commandAckNumber, commandDetails);
  }
  
//  
//
//  private Map<String, String> createClientVariablesMap(String commandString) {
//    System.out.println("RCam Distributed Backend - CommandFactory - createClientVariablesMap is never called.");
//    Map<String, String> clientVariables = new HashMap<String, String>();
//    int variablesSubstringStart = commandString.indexOf("{");
//    int variablesSubstringEnd = commandString.indexOf("}");
//    
//    if(variablesSubstringStart > 0 && variablesSubstringEnd > 0 && variablesSubstringEnd <= commandString.length()) {
//      String[] clientVariableArray;
//      if(commandString.indexOf(",") > 0) {
//        clientVariableArray = commandString.substring(variablesSubstringStart+1, variablesSubstringEnd).split(",");
//      } else {
//        clientVariableArray = new String[1];
//        clientVariableArray[0] = commandString.substring(variablesSubstringStart+1, variablesSubstringEnd);
//      }
//      
//      for(String clientVariable : clientVariableArray) {
//        if(clientVariable.indexOf(":") > 0) {
//          String[] variableAndValue = clientVariable.split(":");
//          clientVariables.put(variableAndValue[0], variableAndValue[1]);
//        }
//      }
//    }
//    
//    return clientVariables;
//  }
//  
  

}
