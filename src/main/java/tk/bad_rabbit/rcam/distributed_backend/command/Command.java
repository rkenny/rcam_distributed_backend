package tk.bad_rabbit.rcam.distributed_backend.command;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.util.List;
import java.util.Map;
import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ICommandResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.state.DoneState;
import tk.bad_rabbit.rcam.distributed_backend.command.state.ICommandState;




public class Command extends ACommand {
  private List<String> commandString;
  private String commandName;
  private Map<String, String> clientVariables;
  private Map<String, String> commandVariables;
  private Map<String, String> serverVariables;
  private Integer commandAckNumber;
  private volatile ICommandState state;
  private volatile ICommandResponseAction commandResponseAction;
  private String returnCode;
  
  public Command(String commandName, Integer commandAckNumber, List<String> commandString, Map<String, String> clientVariables,
      Map<String, String> commandVariables, Map<String, String> serverVariables, ICommandResponseAction commandResponseAction) {
    this.commandName = commandName;
    this.commandString = commandString;
    this.clientVariables = clientVariables;
    this.commandVariables = commandVariables;
    this.serverVariables = serverVariables;
    this.commandAckNumber = commandAckNumber;
    this.commandResponseAction = commandResponseAction;
  }

  public void doAction(Observer actionObserver, ICommandState commandState) {
    if(commandState.getClass().getSimpleName().equals(this.state.getClass().getSimpleName())) {
      this.state.doAction(actionObserver, this);
    }  
  }
  
  public ICommandState setState(ICommandState state) {
    
    this.state = state;
    
    
    setChanged();
    notifyObservers(state);
    
    
    return state;
  }
  
  public ICommandState getState() {
    return this.state;
  }
  
  public String getClientVariable(String variable) {
    return this.clientVariables.get(variable);
  }
  
  public String getCommandVariable(String variable) {
    return this.commandVariables.get(variable);
  }
  
  public String getServerVariable(String variable) {
    return this.serverVariables.get(variable);
  }
   
  public String getReturnCode() {
    return returnCode;
  }
  
  public void performCommandResponseAction(Object actionObject) {
    commandResponseAction.doAction(actionObject, this);
  }
  
  public void setReturnCode(String returnCode) {
    this.returnCode = returnCode;
  }
  
  public Boolean isType(String type) {
    return commandName == type;
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
    return CharBuffer.wrap(commandName + "[" + commandAckNumber.toString() + "]" + finalizeCommandString() + '\n');
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
    commandVariables.put("returnCode", Integer.toString(p.exitValue()));
    
    this.setState(new DoneState());
    
    
    
    return new Pair<Integer, Integer>(this.commandAckNumber, p.exitValue());
  }

}
