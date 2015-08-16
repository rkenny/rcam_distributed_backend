package tk.bad_rabbit.rcam.distributed_backend.command;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observer;

import org.json.JSONObject;

import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ICommandResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.state.DoneState;
import tk.bad_rabbit.rcam.distributed_backend.command.state.ICommandState;




public class Command extends ACommand {
  private List<String> commandString;
  private String commandName;
  private JSONObject clientVariables;
  private Map<String, String> commandVariables;
  private Map<String, String> serverVariables;
  private Integer commandAckNumber;
  private volatile ICommandState state;
  private volatile ICommandResponseAction commandResponseAction;
  private String returnCode;
  
  public Command(String commandName, Integer commandAckNumber, List<String> commandString, JSONObject clientVariables,
      Map<String, String> commandVariables, Map<String, String> serverVariables, ICommandResponseAction commandResponseAction) {
    this.commandName = commandName;
    this.commandString = commandString;
    this.clientVariables = clientVariables;
    this.commandVariables = commandVariables;
    this.serverVariables = serverVariables;
    this.commandAckNumber = commandAckNumber;
    this.commandResponseAction = commandResponseAction;
    
    System.out.println("Created command " + commandName + "[" + commandAckNumber + "]" + clientVariables);
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
  
  public Object getClientVariable(String variable) {
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
    
    Iterator<String> clientVariableIterator = clientVariables.keys();
    while(clientVariableIterator.hasNext()) {
      String key = clientVariableIterator.next();
      finalCommandString = finalCommandString.replace("&"+key, clientVariables.get(key).toString());
    }
    
    for(String key : commandVariables.keySet()) {
      finalCommandString = finalCommandString.replace("@"+key, commandVariables.get(key));
    }
    for(String key : serverVariables.keySet()) {
      finalCommandString = finalCommandString.replace("$"+key, serverVariables.get(key));
    }
    finalCommandString = finalCommandString.replaceFirst("\\[", "(");
    finalCommandString = finalCommandString.concat(")");

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
  
  public void setupEnvironment(Map<String, String> environment) {
    for(String key : serverVariables.keySet()) {
      environment.put(key, serverVariables.get(key));
    }     
     
     for(String key : commandVariables.keySet()) {
       environment.put(key, commandVariables.get(key));
     }
         
     Iterator<String> clientVariableIterator = clientVariables.keys();
     while(clientVariableIterator.hasNext()) {
       String key = clientVariableIterator.next();
       environment.put(key, clientVariables.get(key).toString());
     }
  }

  public Pair<Integer, Integer> call() throws Exception {
    String[] command = {"./config/commands/"+commandName+"/command"};
    System.out.println(command);
    ProcessBuilder pb = new ProcessBuilder(command);
    
    
    setupEnvironment(pb.environment());
    
    Process process = pb.start();
    
    //Read out dir output
    InputStream is = process.getInputStream();
    InputStreamReader isr = new InputStreamReader(is);
    BufferedReader br = new BufferedReader(isr);
    String line;
    System.out.printf("Output of running %s is:\n", Arrays.toString(command));
    while ((line = br.readLine()) != null) {
        System.out.println(line);
    }
    
    //Wait to get exit value
    Integer exitValue = null;
    try {
      exitValue = process.waitFor();
      commandVariables.put("returnCode", Integer.toString(exitValue));
      this.setState(new DoneState());
      System.out.println("\n\nExit Value is " + exitValue);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
     
    return new Pair<Integer, Integer>(this.commandAckNumber, exitValue);
  }

}
