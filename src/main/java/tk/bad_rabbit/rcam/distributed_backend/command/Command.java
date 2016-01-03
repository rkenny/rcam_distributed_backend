package tk.bad_rabbit.rcam.distributed_backend.command;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.json.JSONArray;
import org.json.JSONObject;

import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.ACommandResponseAction;
import tk.bad_rabbit.rcam.distributed_backend.command.state.CommandReadyToReduceState;
import tk.bad_rabbit.rcam.distributed_backend.command.state.DoneState;
import tk.bad_rabbit.rcam.distributed_backend.command.state.ICommandState;
import tk.bad_rabbit.rcam.distributed_backend.server.ServerThread;


public class Command extends ACommand {
  private String commandName;
  private JSONObject clientVariables;
  
  private volatile JSONObject commandConfiguration;
  private JSONObject serverVariables;
  private Integer commandAckNumber;
  private volatile ICommandState state;

  private String returnCode;
  
  public Command(String commandName, Integer commandAckNumber, JSONObject commandConfiguration, JSONObject clientVariables,
      JSONObject serverVariables) {
    this.commandName = commandName;
    
    this.clientVariables = clientVariables;
    this.commandConfiguration = commandConfiguration;
    this.serverVariables = serverVariables;
    this.commandAckNumber = commandAckNumber;
    //this.commandResponsNetworkeAction = commandResponsNetworkeAction;
    
    System.out.println("Created command " + commandName + "[" + commandAckNumber + "]" + clientVariables);
  }

  public void doNetworkAction(Observer actionObserver, ICommandState commandState) {
    this.state.doNetworkAction(actionObserver, this);
  }
  
  public void doRelatedCommandAction(Observer actionObserver, ICommandState commandState) {
    System.out.println("RCam Distributed Backend - Command - doRelatedCommandAction called");
    commandState.doRelatedCommandAction(actionObserver, this);
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
  
  public Object getServerVariable(String variable) {
    return this.serverVariables.get(variable);
  }
   
  public String getReturnCode() {
    return this.commandConfiguration.get("returnCode").toString();
  }
  
  //public void performCommandResponseNetworkAction(Observer actionObject) {
//    commandResponsNetworkeAction.doNetworkAction(actionObject, this);
//  }
  
  //public void performCommandResponseRelatedCommandAction(Observer actionObject) {
//    commandResponsNetworkeAction.doRelatedCommandAction(actionObject, this);
//  }
  
  
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
    StringBuilder finalCommandString = new StringBuilder();

    finalCommandString.append("{");

    
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
    
    return finalCommandString.toString();
  }
  
  public String getCommandName() {
    return commandName;
  }

  public Integer getAckNumber() {
    return commandAckNumber;
  }
  
  public CharBuffer asCharBuffer() {
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
    System.out.println("RCam Distributed Backend - Command - " + getCommandName() + "[" + getAckNumber() + "] has been call()ed");
    String[] command = {commandConfiguration.getString(this.state.getStateExecutableType())};
    ProcessBuilder pb = new ProcessBuilder(command);
    
    setupEnvironment(pb.environment());
    
    Process process = pb.start();
    
    InputStream is = process.getInputStream();
    InputStreamReader isr = new InputStreamReader(is);
    BufferedReader br = new BufferedReader(isr);
    String line;

    while ((line = br.readLine()) != null) {
        System.out.println(line);
    }
    
    //Wait to get exit value
    Integer exitValue = null;
    try {
      exitValue = process.waitFor();
      commandConfiguration.put("returnCode", Integer.toString(exitValue));
      this.setState(new CommandReadyToReduceState());
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
     
    return new Pair<Integer, Integer>(this.getAckNumber(), exitValue);
      
  }

  public void update(Observable serverThread, Object arg) {
    //if(serverThread instanceof ServerThread) {
    //  ((ServerThread) serverThread).doAction(this, arg);
   // }
  }


  
}
