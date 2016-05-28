package tk.bad_rabbit.rcam.distributed_backend.command.action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.util.concurrent.Callable;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.commandcontroller.CommandController;
import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.IConfigurationProvider;
import tk.bad_rabbit.rcam.distributed_backend.controller.RunController;

public class ReductionCompleteCommandAction  implements ICommandAction, IRelatedCommandAction  {

  private String commandName;
  private Integer ackNumber;
  private Integer relatedAckNumber;
  
  public void setCommandDetails(ACommand aCommand) {
    this.commandName = aCommand.getCommandName();
    this.ackNumber = aCommand.getAckNumber();
    relatedAckNumber = (Integer) aCommand.getClientVariable("ackNumber");
  }

  public CharBuffer asCharBuffer() {
    // TODO Auto-generated method stub
    return null;
  }

  public void takeNecessaryInfo(IConfigurationProvider configurationProvider) {
    // TODO Auto-generated method stub

  }

  public ICommandAction nextAction() {
    // TODO Auto-generated method stub
    return null;
  }

  public Callable<Integer> getRelatedCallable(final CommandController commandController) {
    synchronized(this) {
      return new Callable<Integer>() {
        public Integer call() {
          commandController.getCommand(relatedAckNumber).getSemaphore().release();
          return 0;
        }
      };
    }
  }
  
  
}
