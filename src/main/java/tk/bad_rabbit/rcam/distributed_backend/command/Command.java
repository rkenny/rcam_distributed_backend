package tk.bad_rabbit.rcam.distributed_backend.command;

import java.nio.CharBuffer;
import java.util.List;
import java.util.Map;

public class Command implements ICommand {
  private List<String> commandString;
  private String commandName;
  private Map<String, String> clientVariables;
  
  public Command(String commandName, List<String> commandString, Map<String, String> clientVariables) {
    this.commandName = commandName;
    this.commandString = commandString;
    this.clientVariables = clientVariables;
  }
  
  public String finalizeCommandString() {
    String finalCommandString = commandString.toString();
    for(String key : clientVariables.keySet()) {
      finalCommandString = finalCommandString.replace("&"+key, clientVariables.get(key));
    }
    return finalCommandString;
  }
  
  public void run() {
    System.out.println(commandName + " " + finalizeCommandString());
  }

  public CharBuffer asCharBuffer() {
    // TODO Auto-generated method stub
    return null;
  }

}
