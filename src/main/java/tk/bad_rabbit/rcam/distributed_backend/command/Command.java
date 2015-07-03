package tk.bad_rabbit.rcam.distributed_backend.command;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.util.List;
import java.util.Map;




public class Command implements ICommand {
  private List<String> commandString;
  private String commandName;
  private Map<String, String> clientVariables;
  private Map<String, String> commandVariables;
  private Map<String, String> serverVariables;
  private Integer commandAckNumber;
  private CommandState state;
  
  public Command(String commandName, Integer commandAckNumber, List<String> commandString, Map<String, String> clientVariables,
      Map<String, String> commandVariables, Map<String, String> serverVariables) {
    this.commandName = commandName;
    this.commandString = commandString;
    this.clientVariables = clientVariables;
    this.commandVariables = commandVariables;
    this.serverVariables = serverVariables;
    this.commandAckNumber = commandAckNumber;
    this.state = CommandState.NEW;
  }

  public ICommand setReadyToExecute() {
    this.state = CommandState.READY_TO_EXECUTE;
    return this;
  }
  
  public Boolean isReadyToExecute() {
    return this.isInState(CommandState.READY_TO_EXECUTE);
  }
  
  public ICommand setDone() {
    this.state = CommandState.DONE;
    return this;
  }
  
  public Boolean isInState(CommandState state) {
    return this.state == state;
  }
  
  public Boolean isReadyToSend() {
    return (isIgnored() && this.state == CommandState.NEW || !isIgnored() && this.state == CommandState.READY_TO_SEND);
  }
  
  public ICommand readyToSend() {
    this.state = CommandState.READY_TO_SEND;
    return this;
  }
  
  public Boolean wasSent() {
    return this.state == CommandState.SENT || this.state == CommandState.AWAITING_ACK || this.state == CommandState.DONE;
  }
  
  public ICommand setSent() {
    this.state = isIgnored() ? CommandState.SENT : CommandState.AWAITING_ACK;
    return this;
  }
  
  public ICommand wasReceived() {
    this.state = CommandState.RECEIVED;
    return this;
  }
 ;
  public ICommand wasAcked() {
    this.state = CommandState.ACKED;
    return this;
  }
  
  public ICommand commandError() {
    this.state = CommandState.ERROR;
    return this;
  }
  
  public Boolean isIgnored() {
    return commandVariables.get("ignored") == "true";
  }
  
  
  
  public String finalizeCommandString() {
    //String finalCommandString = commandString.toString();
    StringBuilder stringBuilder = new StringBuilder();
    for(String aString : commandString) {
      stringBuilder.append(" ").append(aString);
    }
    String finalCommandString = stringBuilder.toString().trim();
    for(String key : clientVariables.keySet()) {
      finalCommandString = finalCommandString.replace("&"+key, clientVariables.get(key));
    }
    for(String key : commandVariables.keySet()) {
      finalCommandString = finalCommandString.replace("@"+key, commandVariables.get(key));
    }
    for(String key : serverVariables.keySet()) {
      finalCommandString = finalCommandString.replace("$"+key, serverVariables.get(key));
    }
    //finalCommandString = finalCommandString.replaceFirst("\\[", "(");
    //finalCommandString = finalCommandString.concat(")");

    return finalCommandString;
  }
  
  public String getCommandName() {
    return commandName;
  }

  public Integer getAckNumber() {
    return commandAckNumber;
  }
  
  public CharBuffer asCharBuffer() {
    // TODO Auto-generated method stub
    return CharBuffer.wrap(commandName + "[" + commandAckNumber.toString() + "]" + finalizeCommandString());
  }

  public Pair<Integer, Integer> call() throws Exception {
    String thisCommandString = finalizeCommandString();
   // thisCommandString = thisCommandString.substring(1, thisCommandString.length() -1 );
    System.out.println(commandName + " [" + thisCommandString + "]");
    
    Process p = Runtime.getRuntime().exec(thisCommandString);
     
    //CommandResult result = new CommandResult(commandName);
    
    p.waitFor();
    
    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
    StringBuilder sb = new StringBuilder();
    String line = "";     
    while ((line = reader.readLine())!= null) {
      sb.append(line + "\n");
    }
    
    System.out.println(sb);
    
    return new Pair<Integer, Integer>(this.commandAckNumber, p.exitValue());
  }

}
