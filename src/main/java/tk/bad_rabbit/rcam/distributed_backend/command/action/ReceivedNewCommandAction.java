package tk.bad_rabbit.rcam.distributed_backend.command.action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.util.concurrent.Callable;

import org.json.JSONObject;

import tk.bad_rabbit.rcam.distributed_backend.client.ClientThread;
import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.IConfigurationProvider;
import tk.bad_rabbit.rcam.distributed_backend.controller.RunController;

public class ReceivedNewCommandAction implements INetworkAction, ICommandAction {
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
  
  
  
  
  public Callable<Integer> getNetworkCallable(final ClientThread clientThread) {
    synchronized(this) {
      final ICommandAction thisCommandAction = this;
      final CharBuffer ack = this.asCharBuffer();
      return new Callable<Integer>() {
        public Integer call() { 
          clientThread.send(ack);
          return 0;
        }
      };
    }
  }
  
  
  
  public CharBuffer asCharBuffer() {
    return createAck();
  }
  
  public CharBuffer createAck() {
    JSONObject ack = new JSONObject();
    JSONObject commandVariables = new JSONObject();
    commandVariables.put("commandName", commandName);
    commandVariables.put("ackNumber", ackNumber);
    ack.put("commandName", "Ack");
    ack.put("details", commandVariables);
    return CharBuffer.wrap(ack.toString()+"\n");
  }
  
  public ICommandAction nextAction() {
    if(commandName.toLowerCase().equals("reducecomplete")) {
      return new ReductionCompleteCommandAction();
    }
    return new RunNewCommandAction();
  }
  
}
