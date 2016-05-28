package tk.bad_rabbit.rcam.distributed_backend.command.action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;

import org.json.JSONObject;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.IConfigurationProvider;
import tk.bad_rabbit.rcam.distributed_backend.controller.RunController;

public class RunNewCommandAction implements ICommandAction, IRunResponseAction  {

  private String commandName;
  private Integer ackNumber;
  private String executable;
  private JSONObject commandDetails;
  
  public void setCommandDetails(ACommand aCommand) {
    this.commandName = aCommand.getCommandName();
    this.ackNumber = aCommand.getAckNumber();
    if(aCommand.getClientVariables() != null) {
      this.commandDetails = aCommand.getClientVariables();
    }
  }

  public CharBuffer asCharBuffer() {
    // TODO Auto-generated method stub
    return null;
  }

  public void takeNecessaryInfo(IConfigurationProvider configurationProvider) {
    if(configurationProvider.getCommandConfiguration(commandName).getJSONObject("executables").has(this.getClass().getSimpleName())) {
      this.executable = configurationProvider.getCommandConfiguration(commandName).getJSONObject("executables").getString(this.getClass().getSimpleName());
    }
  }
  

  public ICommandAction nextAction() {
    return new ReportReadyToReduceAction();
  }

  private void setupEnvironment(Map<String, String> environment) {
    
    //Iterator<String> serverVariableIterator = serverVariables.keys();
    //while(serverVariableIterator.hasNext()) {
    //  String key = serverVariableIterator.next();
    //  environment.put(key, serverVariables.get(key).toString());
    //}     
     
    //Iterator<String> variableIterator = commandConfiguration.getJSONObject("commandVars").keys();
    //while(variableIterator.hasNext()) {
    //  String key = variableIterator.next();
    //  environment.put(key, commandConfiguration.getJSONObject("commandVars").get(key).toString());
    //}
         
     Iterator<String> clientVariableIterator = commandDetails.keys();
     while(clientVariableIterator.hasNext()) {
       String key = clientVariableIterator.next();
       environment.put(key, commandDetails.get(key).toString());
     }
  }
  
  public Callable<Integer> getRunCallable(final RunController runController) {
    synchronized(this) {
      final ICommandAction thisCommandAction = this;
      final String thisExecutable = executable;
      return new Callable<Integer>() {
        public Integer call() {
          ProcessBuilder pb = new ProcessBuilder(thisExecutable);
          Process process;
          try {
            setupEnvironment(pb.environment());
            process = pb.start();
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
            process.waitFor();
            return process.exitValue();
        
          } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
          } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
          
          
          return null;
        }
      };
    }
  }
  
}
