package tk.bad_rabbit.rcam.distributed_backend.command;

import java.util.Observable;
import java.util.Observer;

public abstract class ACommand extends Observable implements ICommand, Observer {

  @Override
  public void notifyObservers(Object arg) {
    synchronized(this) {
      super.notifyObservers(arg);
//      try {
//        this.wait();
//      } catch (InterruptedException e) {
//         TODO Auto-generated catch block
//        e.printStackTrace();
//      }
    }
  }


}