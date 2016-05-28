package tk.bad_rabbit.rcam.distributed_backend.commandcoordinator;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import tk.bad_rabbit.rcam.distributed_backend.client.ClientThread;
import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.action.ICommandAction;
import tk.bad_rabbit.rcam.distributed_backend.commandcontroller.CommandController;
import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.IConfigurationProvider;
import tk.bad_rabbit.rcam.distributed_backend.controller.RunController;


public class CommandCoordinator implements Observer {
  ClientThread clientThread;
  RunController runController;
  CommandController commandController;
  IConfigurationProvider configurationProvider;
  
  public CommandCoordinator(ClientThread clientThread, RunController runController, CommandController commandController, IConfigurationProvider configurationProvider) {
    this.clientThread = clientThread;
    this.runController = runController;
    this.configurationProvider = configurationProvider;
    this.commandController = commandController;
  }
  
  public void update(Observable o, Object a) {
    System.out.println("CommandCoordinator observed a change in " + o.getClass().getSimpleName() + " of type " + a.getClass().getSimpleName());
    synchronized(o) {
      if(o instanceof ACommand) {
        ACommand command = (ACommand) o;
        ICommandAction pendingAction = command.peekNextAction();
        pendingAction.takeNecessaryInfo(configurationProvider);
        
        
        Future<Integer> clientAction = clientThread.handleAction(pendingAction);
        Future<Integer> controllerAction = runController.handleAction(pendingAction);
        Future<Integer> commandAction = commandController.handleAction(pendingAction);
        try {
          if(clientAction != null) { clientAction.get(10, TimeUnit.MINUTES); }
          if(controllerAction != null) { controllerAction.get(10, TimeUnit.MINUTES); }
          if(commandAction != null) { commandAction.get(10, TimeUnit.MINUTES); }
        } catch (InterruptedException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (ExecutionException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (TimeoutException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        System.out.println("CommandCoordinator will perform " + pendingAction.getClass().getSimpleName());
      }
    }
  }
}
