package tk.bad_rabbit.rcam.distributed_backend.command.state;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.client.ClientThread;
import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.server.ServerThread;


public class ReceivedCommandState extends ACommandState {

  public String getStateExecutableType() {
    return "commandExecutable";
  }
  public void doNetworkStuff(Observer actionObserver, ACommand actionSubject) {
    //if(actionObserver instanceof ClientThread ) {
      //((ClientThread) actionObserver).send((ACommand) actionSubject);
      ((ACommand) actionSubject).performCommandResponseNetworkAction(actionObserver);
    //}
  }
  
  public void doRelatedCommandStuff(Observer actionObserver, ACommand actionSubject) {
    //((ACommand) actionSubject).performCommandResponseRelatedCommandAction(actionObserver);
  }

}
