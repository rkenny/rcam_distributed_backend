package tk.bad_rabbit.rcam.distributed_backend.command.state;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;

public abstract class ACommandState implements ICommandState {
  
  public void doNetworkAction(Observer actionObserver, ACommand actionSubject){
    System.out.println("RCam Distributed Backend - "+ this.getClass().getSimpleName() +" doNetworkAction called");
    doNetworkStuff(actionObserver, actionSubject);
  }
  
  public void doRelatedCommandAction(Observer actionObserver, ACommand actionSubject) {
    System.out.println("RCam Distributed Backend - "+ this.getClass().getSimpleName() +" doRelatedCommandAction called");
    doRelatedCommandStuff(actionObserver, actionSubject);
  }
}
