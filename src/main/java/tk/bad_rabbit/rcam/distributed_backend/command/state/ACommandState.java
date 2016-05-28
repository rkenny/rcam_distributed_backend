package tk.bad_rabbit.rcam.distributed_backend.command.state;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Future;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.responseactions.DefaultCommandResponseAction;

public abstract class ACommandState extends Observable implements ICommandState{
  Future networkResponseActionResult;
  Future relatedCommandActionResult;
  Future runCommandActionResult;
  
  public void notifyObserversIfFinished() {
    if(runCommandActionResult != null && networkResponseActionResult != null && relatedCommandActionResult != null) {
      if(runCommandActionResult.isDone() && networkResponseActionResult.isDone() && relatedCommandActionResult.isDone()) {
        setChanged();
        notifyObservers(this);
      }
    }
  }
  
  
  public ACommandState() {
    setNetworkResponseAction(new DefaultCommandResponseAction());
    setRelatedCommandResponseAction(new DefaultCommandResponseAction());
    setRunCommandResponseAction(new DefaultCommandResponseAction());
  }
  
  public void doNetworkAction(Observer actionObserver, ACommand actionSubject) {  
    networkResponseActionResult = getNetworkResponseAction().doStuff(actionObserver, actionSubject);
    notifyObserversIfFinished();
  }
  
  public void doRelatedCommandAction(Observer actionObserver, ACommand actionSubject) {
    relatedCommandActionResult = getRelatedCommandResponseAction().doStuff(actionObserver, actionSubject);
    notifyObserversIfFinished();
  }
  

  
  public void doRunCommandAction(Observer actionObserver, ACommand actionSubject) {
    
    System.out.println("RCam Distributed Backend " + this.getClass().getSimpleName() + " response action is " + this.getRunCommandResponseAction().getClass().getSimpleName());
    System.out.println("RCam Distributed Backend " + this.getClass().getSimpleName() + " Will do its run command response action");
    runCommandActionResult = getRunCommandResponseAction().doStuff(actionObserver, actionSubject);
    notifyObserversIfFinished();
  }
  
  public abstract ACommandState getNextState();
    
  
}
