package tk.bad_rabbit.rcam.distributed_backend.command;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Stack;
import java.util.concurrent.Semaphore;

import org.json.JSONObject;

import tk.bad_rabbit.rcam.distributed_backend.command.action.ICommandAction;

public abstract class ACommand extends Observable implements ICommand, Observer {
  Semaphore semaphore;
  
  public abstract JSONObject getClientVariables();
  
  public void setSemaphore(Semaphore semaphore) {
    this.semaphore = semaphore;
  }
  
  public Semaphore getSemaphore() {
    return this.semaphore;
  }
  
  public ACommand() {
    this.pendingCommandActions = new Stack<ICommandAction>();
    this.semaphore = new Semaphore(1);
  }
  
  public void update(Observable serverThread, Object arg) {}
  
  public void doNetworkAction(Observer actionObserver) {
    System.out.println(this.getCommandName() + " doNetworkAction called");
    this.getState().doNetworkAction(actionObserver, this);
  }
  public void doRelatedCommandAction(Observer actionObserver) {
    System.out.println(this.getCommandName() + " doRelatedCommandAction called");
    this.getState().doRelatedCommandAction(actionObserver, this);
  }
  
  public void doRunCommandAction(Observer actionObserver) {
    System.out.println(this.getCommandName() + " doRunCommandAction called");
    this.getState().doRunCommandAction(actionObserver, this);
  }
  
  public void nextState() {
    System.out.println("nextState called");
    this.setState(this.getState().getNextState());
  }
  
  public void addObservers(List<Observer> observers) {
    System.out.println(this.getCommandName() + " adding "+observers.size()+" observers");
    for(Observer observer : observers) {
      this.addObserver(observer);
    }
  }
  
  Stack<ICommandAction> pendingCommandActions;
  public ICommandAction peekNextAction() {
    return this.pendingCommandActions.peek();
  }
  
  public void addPendingAction(ICommandAction newAction) {
    newAction.setCommandDetails(this);
    this.pendingCommandActions.push(newAction);
    setChanged();
    notifyObservers(newAction);
  }
  
  @Override
  public void notifyObservers(Object arg) {
    synchronized(this) {
      super.notifyObservers(arg);
    }
    
    if(pendingCommandActions.size() >= 1 && pendingCommandActions.peek().nextAction() != null) {
      addPendingAction(pendingCommandActions.pop().nextAction());
    }

    
  }


}