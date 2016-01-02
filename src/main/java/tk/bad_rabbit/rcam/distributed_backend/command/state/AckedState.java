package tk.bad_rabbit.rcam.distributed_backend.command.state;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.controller.Controller;



public class AckedState extends ACommandState {
  public String getStateExecutableType() {
    return "commandExecutable";
  }
  
  public void doNetworkStuff(Observer actionObserver, ACommand actionSubject) {
    System.out.println("RCam Distributed Backend - AckedState - doNetworkStuff for " + actionSubject.getCommandName() + "[" + actionSubject.getAckNumber() + "]");
  }
  
  public void doRelatedCommandStuff(Observer actionObserver, ACommand actionSubject) {
    System.out.println("RCam Distributed Backend - AckedState - doRelatedCommandStuff for " + actionSubject.getCommandName() + "[" + actionSubject.getAckNumber() + "]");
    if(actionObserver instanceof Controller) {
      ((Controller) actionObserver).runCommand((ACommand) actionSubject);
    }
  }
  
  public void nextState(ACommand actionSubject) {
    //actionSubject.setState(new ReadyToReduceState());
  }

}
