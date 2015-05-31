package tk.bad_rabbit.rcam.distributed_backend.commandfactory;

import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tk.bad_rabbit.rcam.distributed_backend.command.AckCommand;
import tk.bad_rabbit.rcam.distributed_backend.command.Command;
import tk.bad_rabbit.rcam.distributed_backend.command.ErrorCommand;
import tk.bad_rabbit.rcam.distributed_backend.command.ICommand;

public class CommandFactory implements ICommandFactory {

  Map<String, List<String>> commandConfigurations;
  Map<String, Map<String, String>> commandVariables;
  Map<String, String> serverVariables;
  
  public CommandFactory(Map<String, List<String>> commandConfigurations, Map<String,
      Map<String, String>> commandVariables, Map<String, String> serverVariables) {
    this.commandConfigurations = commandConfigurations;
    this.commandVariables = commandVariables;
    this.serverVariables = serverVariables;
  }
  
  public ICommand createCommand(CharBuffer commandCharBuffer) {    
    return createCommand(commandCharBuffer.toString());
  }
  
  
  public ICommand createCommand(String commandString) {
    ICommand command = null;
    
    String commandType;
    int commandTypeLength = commandString.indexOf("(") > 0 ? commandString.indexOf("(") : commandString.length();
    commandType = commandString.substring(0, commandTypeLength).trim();
    
    if(commandConfigurations.containsKey(commandType)) {
      command = new Command(commandType, commandConfigurations.get(commandType), createClientVariablesMap(commandString),
          commandVariables.get(commandType), serverVariables);
    } else if(commandString.equals("Ack")) {
      command = new AckCommand();
    } else if(commandString.equals("Error")) {
      command = new ErrorCommand();
    } else {
      // won't hit this yet
      System.out.println("Won't instantiate that command [" +commandString+ "]");
    }      
    
    return command;
  }

  private Map<String, String> createClientVariablesMap(String commandString) {
    Map<String, String> clientVariables = new HashMap<String, String>();
    int variablesSubstringStart = commandString.indexOf("(");
    int variablesSubstringEnd = commandString.indexOf(")");
    
    if(variablesSubstringStart > 0 && variablesSubstringEnd > 0 && variablesSubstringEnd <= commandString.length()) {
      String[] clientVariableArray;
      if(commandString.indexOf(",") > 0) {
        clientVariableArray = commandString.substring(variablesSubstringStart+1, variablesSubstringEnd).split(",");
      } else {
        clientVariableArray = new String[1];
        clientVariableArray[0] = commandString.substring(variablesSubstringStart+1, variablesSubstringEnd);
      }
      
      for(String clientVariable : clientVariableArray) {
        if(clientVariable.indexOf("=") > 0) {
          String[] variableAndValue = clientVariable.split("=");
          clientVariables.put(variableAndValue[0], variableAndValue[1]);
        }
      }
    }
    
    return clientVariables;
  }
  
  

}
