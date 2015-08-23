package tk.bad_rabbit.rcam.distributed_backend.commandfactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.json.JSONObject;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.Command;
import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.IConfigurationProvider;

public class CommandFactory implements ICommandFactory {
  String commandConfigurationPath;
  Map<String, JSONObject> commandConfigurations;
  
  JSONObject serverVariables;
  Random rand;
  
  IConfigurationProvider configurationProvider;
  
  public CommandFactory(Map<String, JSONObject> commandConfigurations, JSONObject serverVariables, IConfigurationProvider configurationProvider) {
    this.commandConfigurationPath = configurationProvider.getCommandConfigurationPath();
    
    this.commandConfigurations = commandConfigurations;
    this.serverVariables = serverVariables;
    rand = new Random();
    this.configurationProvider = configurationProvider;
  }
  
//  public ACommand createResultCommand(Pair<Integer, Integer> commandResult) {
//    return createCommand("CommandResult(ackNumber="+commandResult.getLeft()+",resultCode="+commandResult.getRight()+")");
//  }
  
  public JSONObject createCommandConfiguration(String commandType) {
    StringBuilder commandArgs = new StringBuilder();
    //
    File commandConfigFolder = new File("./config/commands/" + commandType);
    System.out.println("Looking for a file in ./config/commands/"+ commandType);
    if(commandConfigFolder.isDirectory()) {
      File commandConfigFile = new File(commandConfigFolder, "command");
      System.out.println("Found the command file for " + commandType);
      
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
    System.out.println("commandArgs is " + commandArgs.toString());
    return new JSONObject(commandArgs.toString());
  }
  
  public ACommand createCommand(String commandType, Integer ackNumber, JSONObject clientVariables) {
    ACommand command = null;
    //if(commandConfigurations.containsKey(commandType)) {
    command = new Command(commandType, ackNumber, createCommandConfiguration(commandType), clientVariables,
          serverVariables, configurationProvider.getCommandResponseAction(commandType));
      //command = new Command(commandType, ackNumber, commandConfigurations.get(commandType), clientVariables,
      //    serverVariables, configurationProvider.getCommandResponseAction(commandType));
      //System.out.println("Command generated");
    //} else {
    //  System.out.println("no command generated");
    //}
    return command;
  }
  
  public ACommand createResultCommand(ACommand command) {
    System.out.println("Attempting to create a resultCommand");
    return createCommand("CommandResult{ackNumber:"+command.getAckNumber()+",resultCode:"+command.getReturnCode()+"}");
  }
    
  public ACommand createAckCommand(ACommand command) {
    return createCommand("Ack{command:" + command.getCommandName() + ",ackNumber:"+command.getAckNumber()+"}");
  }
  
  public ACommand createCommand(CharBuffer commandBuffer) {
    return createCommand(commandBuffer.toString());
  }
  
  public ACommand createCommand(String commandString) {
    ACommand command = null;
    
    String commandType;
    int commandTypeLength;
    
    System.out.println("Creating command " + commandString);
    
    commandTypeLength = commandString.indexOf("{") > 0 ? commandString.indexOf("{") : commandString.length();
    commandTypeLength = (commandString.indexOf("[") < commandTypeLength  
        && commandString.indexOf("[") > 0 )? commandString.indexOf("[") : commandTypeLength;
    commandType = commandString.substring(0, commandTypeLength).trim();
      
    
    
    Integer commandAckNumber;
    if(commandString.indexOf("[") > 0 && commandString.indexOf("[") < commandString.indexOf("{") ) {
      commandAckNumber = Integer.parseInt(commandString.substring(commandString.indexOf("[")+1, commandString.indexOf("]")));
    } else {
      commandAckNumber = rand.nextInt((99999 - 10000) + 1) + 10000;
    }
    
    JSONObject clientVariables = new JSONObject(commandString.substring(commandString.indexOf("{"), commandString.length()));
    //Something => Something[12345] => Ack(Something[12345])
    //Record(duration=200) => Record[123456](duration=200) => Ack(Record[123456])
    
    //Ack[12345](@Commmand[@AckNumber])
    //Ack(command=Record,ackNumber=23456)
    return createCommand(commandType, commandAckNumber, clientVariables);
  }
  
  

  private Map<String, String> createClientVariablesMap(String commandString) {
    Map<String, String> clientVariables = new HashMap<String, String>();
    int variablesSubstringStart = commandString.indexOf("{");
    int variablesSubstringEnd = commandString.indexOf("}");
    
    if(variablesSubstringStart > 0 && variablesSubstringEnd > 0 && variablesSubstringEnd <= commandString.length()) {
      String[] clientVariableArray;
      if(commandString.indexOf(",") > 0) {
        clientVariableArray = commandString.substring(variablesSubstringStart+1, variablesSubstringEnd).split(",");
      } else {
        clientVariableArray = new String[1];
        clientVariableArray[0] = commandString.substring(variablesSubstringStart+1, variablesSubstringEnd);
      }
      
      for(String clientVariable : clientVariableArray) {
        if(clientVariable.indexOf(":") > 0) {
          String[] variableAndValue = clientVariable.split(":");
          clientVariables.put(variableAndValue[0], variableAndValue[1]);
        }
      }
    }
    
    return clientVariables;
  }
  
  

}
