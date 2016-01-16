package tk.bad_rabbit.rcam.distributed_backend.command.state;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.DefaultCommandResponseAction;

public abstract class ACommandState implements ICommandState {
  
  public ACommandState() {
    setNetworkResponseAction(new DefaultCommandResponseAction());
    setRelatedCommandResponseAction(new DefaultCommandResponseAction());
    setRunCommandResponseAction(new DefaultCommandResponseAction());
  }
  
  public void doNetworkAction(Observer actionObserver, ACommand actionSubject){
    getNetworkResponseAction().doStuff(actionObserver, actionSubject);
  }
  
  public void doRelatedCommandAction(Observer actionObserver, ACommand actionSubject) {
    getRelatedCommandResponseAction().doStuff(actionObserver, actionSubject);
  }
  
  public void doRunCommandAction(Observer actionObserver, ACommand actionSubject) {
    getRunCommandResponseAction().doStuff(actionObserver, actionSubject);
  }
  
  
  
}
