package tk.bad_rabbit.rcam.distributed_backend.command.action;

import java.util.concurrent.Semaphore;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;

public abstract class AResponseAction {
  Semaphore semaphore;
  public void setCommandDetails(ACommand command) {
    command.setSemaphore(new Semaphore(1));
    semaphore = command.getSemaphore();
  }
  
  public void acquireSemaphore() {
    try {
      semaphore.acquire();
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  public void releaseSemaphore() {
    semaphore.release();
  }
  
}
