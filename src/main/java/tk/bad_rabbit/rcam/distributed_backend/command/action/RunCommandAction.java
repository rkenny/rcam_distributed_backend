package tk.bad_rabbit.rcam.distributed_backend.command.action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.util.concurrent.Callable;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.IConfigurationProvider;
import tk.bad_rabbit.rcam.distributed_backend.controller.RunController;

public class RunCommandAction implements ICommandAction, IRunResponseAction {

  public Callable<Integer> getRunCallable(RunController runController) {
    synchronized(this) {
    final ICommandAction thisCommandAction = this;
    final String thisExecutable = executable;
      
     return new Callable<Integer>() {
       public Integer call() {         
         ProcessBuilder pb = new ProcessBuilder(thisExecutable);
         Process process;
         try {
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

  private String commandName;
  private Integer ackNumber;
  private String executable;
  
  public void setCommandDetails(ACommand aCommand) {
    this.commandName = aCommand.getCommandName();
    this.ackNumber = aCommand.getAckNumber();
  }

  public void takeNecessaryInfo(IConfigurationProvider configurationProvider) {
    if(configurationProvider.getCommandConfiguration(commandName).getJSONObject("executables").has(this.getClass().getSimpleName())) {
      this.executable = configurationProvider.getCommandConfiguration(commandName).getJSONObject("executables").getString(this.getClass().getSimpleName());
    }
  }

  
  public CharBuffer asCharBuffer() {
    // TODO Auto-generated method stub
    return null;
  }


  public ICommandAction nextAction() {
    return new ReportReadyToReduceAction();
  }

}