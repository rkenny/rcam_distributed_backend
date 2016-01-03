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
    System.out.println("RCam Distributed Backend - "+ this.getClass().getSimpleName() +" doNetworkAction called");
    getNetworkResponseAction().doStuff(actionObserver, actionSubject);
  }
  
  public void doRelatedCommandAction(Observer actionObserver, ACommand actionSubject) {
    System.out.println("RCam Distributed Backend - "+ this.getClass().getSimpleName() +" doRelatedCommandAction called");
    getRelatedCommandResponseAction().doStuff(actionObserver, actionSubject);
  }
  
  public void doRunCommandAction(Observer actionObserver, ACommand actionSubject) {
    System.out.println("RCam Coordinator - " + getClass().getSimpleName() + " - Telling "+ actionObserver.getClass().getSimpleName() + " to doStuff to Command(" + actionSubject.getCommandName()+"["+actionSubject.getAckNumber()+"])");
    getRunCommandResponseAction().doStuff(actionObserver, actionSubject);
  }
  
  
  
}
