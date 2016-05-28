package tk.bad_rabbit.rcam.distributed_backend.command.action;

import java.nio.CharBuffer;
import java.util.concurrent.Callable;

import org.json.JSONObject;

import tk.bad_rabbit.rcam.distributed_backend.client.ClientThread;
import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.IConfigurationProvider;

public class ReportReadyToReduceAction implements ICommandAction, INetworkAction {

  public Callable<Integer> getNetworkCallable(final ClientThread clientThread) {
    synchronized(this) {
      final ICommandAction thisCommandAction = this;
      final CharBuffer readyToReduce = this.asCharBuffer();
      return new Callable<Integer>() {
        public Integer call() { 
          clientThread.send(readyToReduce);
          return 0;
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

  public CharBuffer asCharBuffer() {
    return createReadyToReduce();
  }

  public CharBuffer createReadyToReduce() {
    JSONObject ack = new JSONObject();
    JSONObject commandVariables = new JSONObject();
    commandVariables.put("commandName", commandName);
    commandVariables.put("ackNumber", ackNumber);
    ack.put("commandName", "ReadyToReduce");
    ack.put("details", commandVariables);
    return CharBuffer.wrap(ack.toString()+"\n");
  }
  
  
  public void takeNecessaryInfo(IConfigurationProvider configurationProvider) {
    
  }

  public ICommandAction nextAction() {
    return new AwaitingReductionCompleteAction();
  }

}
