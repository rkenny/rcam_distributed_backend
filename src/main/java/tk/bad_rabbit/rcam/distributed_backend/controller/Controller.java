package tk.bad_rabbit.rcam.distributed_backend.controller;

import tk.bad_rabbit.rcam.distributed_backend.command.ICommand;
import tk.bad_rabbit.rcam.distributed_backend.commandqueue.ICommandQueuer;

public class Controller implements Runnable{
  ICommandQueuer commandQueuer;
  boolean running;
  
  public Controller(ICommandQueuer commandQueuer) {
    this.commandQueuer = commandQueuer; 
  }
  
  
  public void run() {
    running = true;
    ICommand command;
    while(running) {
      try {
        Thread.sleep(125);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      
      while((command = commandQueuer.getNextIncomingCommand()) != null) {
        command.run();
      }
    }
  }
 
}
