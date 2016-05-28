package tk.bad_rabbit.rcam.distributed_backend.command.action;

import java.nio.CharBuffer;
import java.util.concurrent.Callable;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.commandcontroller.CommandController;
import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.IConfigurationProvider;

public class AwaitingReductionCompleteAction extends AResponseAction implements ICommandAction, IRelatedCommandAction {

  private String commandName;
  private Integer ackNumber;
  private String executable;
  
  public void setCommandDetails(ACommand aCommand) {
    super.setCommandDetails(aCommand);
    this.commandName = aCommand.getCommandName();
    this.ackNumber = aCommand.getAckNumber();
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
    return new CleanupAfterReductionAction();
  }

  public Callable<Integer> getRelatedCallable(final CommandController commandController) {
    synchronized(this) {
      return new Callable<Integer>() {
        public Integer call() {
          acquireSemaphore();
          return 0;
        }
      };
    }
  }

}
