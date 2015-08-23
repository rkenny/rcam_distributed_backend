package tk.bad_rabbit.rcam.distributed_backend.command;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Observer;

import org.json.JSONArray;
import org.json.JSONObject;

import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ICommandResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.state.DoneState;
import tk.bad_rabbit.rcam.distributed_backend.command.state.ICommandState;


public class Command extends ACommand {
  private String commandName;
  private JSONObject clientVariables;
  //private JSONObject commandVariables;
  
  private volatile JSONObject commandConfiguration;
  private JSONObject serverVariables;
  private Integer commandAckNumber;
  private volatile ICommandState state;
  private volatile ICommandResponseAction commandResponseAction;
  private String returnCode;
  
  public Command(String commandName, Integer commandAckNumber, JSONObject commandConfiguration, JSONObject clientVariables,
      JSONObject serverVariables, ICommandResponseAction commandResponseAction) {
    this.commandName = commandName;
    
    this.clientVariables = clientVariables;
    this.commandConfiguration = commandConfiguration;
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
  
  //public Object getCommandVariable(String variable) {
  //  return this.commandVariables.get(variable);
  //}
  
  public Object getServerVariable(String variable) {
    return this.serverVariables.get(variable);
  }
   
  public String getReturnCode() {
    System.out.println(commandConfiguration.getJSONObject("commandVars"));
    System.out.println("Getting return code = " + this.commandConfiguration.get("returnCode").toString());
    return this.commandConfiguration.get("returnCode").toString();
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
    return (commandConfiguration.getJSONObject("commandVars").get("ignored") == "true");
  }
  
  
  
  public String finalizeCommandString() {
    //String finalCommandString = commandString.toString();
    StringBuilder finalCommandString = new StringBuilder();
    //System.out.println(clientVariables);
    System.out.println("Finalizing command " + commandName);
    System.out.println("finalCommandString is " + finalCommandString + " before the replaces");
    finalCommandString.append("{");
    
    System.out.println("commandConfiguration is " + commandConfiguration.toString());
    //System.out.println("clientVars is" + commandConfiguration.get("clientVars").toString());
    //System.out.println("clientVariables is " + clientVariables);
    
    if(commandConfiguration.has("clientVars")) {
      JSONArray clientVars = commandConfiguration.getJSONArray("clientVars");
      for(int i = 0; i < clientVars.length(); i++) {
        finalCommandString.append("\""+clientVars.get(i).toString()+"\":");
        // haaaack.
        if(clientVariables.get(clientVars.get(i).toString()) instanceof String) {
          finalCommandString.append("\"");
        }
        finalCommandString.append(clientVariables.get(clientVars.get(i).toString()));
        if(clientVariables.get(clientVars.get(i).toString()) instanceof String) {
          finalCommandString.append("\"");
        }
        finalCommandString.append(",");
      }
    }

    Iterator<String> variableIterator = commandConfiguration.getJSONObject("commandVars").keys();
    while(variableIterator.hasNext()) {
      String key = variableIterator.next();
      //finalCommandString = finalCommandString.replace("@"+key, commandVariables.get(key).toString());
      finalCommandString.append("\"" + key + "\":\"" + commandConfiguration.getJSONObject("commandVars").get(key).toString()+"\"");
      finalCommandString.append(",");
    }
    
    if(commandConfiguration.has("serverVars")) {
      for(int i = 0; i < commandConfiguration.getJSONArray("serverVars").length(); i++) {
      finalCommandString.append("\""+commandConfiguration.getJSONArray("serverVars").get(i).toString()+"\":");
      finalCommandString.append("\""+clientVariables.get(commandConfiguration.getJSONArray("serverVars").get(i).toString())+"\"");
      finalCommandString.append(",");
      }
    }
    
    finalCommandString.deleteCharAt(finalCommandString.length()-1);
    finalCommandString.append("}");
    
    System.out.println("FinalCommandString after is " + finalCommandString + " after the replaces");
    return finalCommandString.toString();
  }
  
  public String getCommandName() {
    return commandName;
  }

  public Integer getAckNumber() {
    System.out.println("Getting ackNumber");
    return commandAckNumber;
  }
  
  public CharBuffer asCharBuffer() {
    // TODO Auto-generated method stub
    return CharBuffer.wrap(commandName + "[" + commandAckNumber.toString() + "]" + finalizeCommandString() + '\n');
  }
  
  public void setupEnvironment(Map<String, String> environment) {
    
    Iterator<String> serverVariableIterator = serverVariables.keys();
    while(serverVariableIterator.hasNext()) {
      String key = serverVariableIterator.next();
      environment.put(key, serverVariables.get(key).toString());
    }     
     
    Iterator<String> variableIterator = commandConfiguration.getJSONObject("commandVars").keys();
    while(variableIterator.hasNext()) {
      String key = variableIterator.next();
      //finalCommandString = finalCommandString.replace("@"+key, commandVariables.get(key).toString());
      environment.put(key, commandConfiguration.getJSONObject("commandVars").get(key).toString());
    }
         
     Iterator<String> clientVariableIterator = clientVariables.keys();
     while(clientVariableIterator.hasNext()) {
       String key = clientVariableIterator.next();
       environment.put(key, clientVariables.get(key).toString());
     }
  }

  public Pair<Integer, Integer> call() throws Exception {
    
    System.out.println("Calling command");
    String[] command = {commandConfiguration.getString("commandExecutable")};
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
      commandConfiguration.put("returnCode", Integer.toString(exitValue));
      System.out.println(commandConfiguration);
      this.setState(new DoneState());
      System.out.println("\n\nExit Value is " + exitValue);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
     
    return new Pair<Integer, Integer>(this.commandAckNumber, exitValue);
  }

}
