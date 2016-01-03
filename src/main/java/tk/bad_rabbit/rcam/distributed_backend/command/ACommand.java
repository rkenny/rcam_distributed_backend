package tk.bad_rabbit.rcam.distributed_backend.command;

import java.util.Observable;
import java.util.Observer;

public abstract class ACommand extends Observable implements ICommand, Observer {

  public void update(Observable serverThread, Object arg) {}
  
  public void doNetworkAction(Observer actionObserver) {
    this.getState().doNetworkAction(actionObserver, this);
  }
  public void doRelatedCommandAction(Observer actionObserver) {
    this.getState().doRelatedCommandAction(actionObserver, this);
  }
  
  @Override
  public void notifyObservers(Object arg) {
    synchronized(this) {
      super.notifyObservers(arg);
    }
  }


}