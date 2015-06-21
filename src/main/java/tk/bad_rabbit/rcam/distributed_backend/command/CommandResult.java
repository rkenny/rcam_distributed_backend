package tk.bad_rabbit.rcam.distributed_backend.command;

import java.nio.CharBuffer;

// this can go into a command like Ack().
public class CommandResult implements ICommand {
  public String commandType;
  private Boolean success;
  private CommandState state;
  
  public CommandResult(String commandType) {
    this.commandType = commandType;
    success = false;
    state = CommandState.NEW;
  }
  
  public ICommand setReadyToExecute() {
    state = CommandState.READY_TO_EXECUTE; // this isn't possible for a CommandResult.
    return this;
  }
  
  public Boolean isReadyToExecute() {
    return false;
  }
  
  public ICommand setDone() {
    state = CommandState.DONE;
    return this;
  }
  
  public Boolean isReadyToSend() {
    return state == CommandState.READY_TO_SEND;
  }
  
  public ICommand readyToSend() {
    state = CommandState.READY_TO_SEND;
    return this;
  }
  public Boolean isInState(CommandState state) {
    return true;
  }
  
  
  public CommandResult setSuccess() {
    this.success = true;
    return this;
  }
  
  public String toString() {
    return "this was toStringed.";
  }
  
  public Boolean isIgnored() {
    return false;
  }
  
  public String notificationCommand()  { 
    return success ? commandType + "(Success)" : commandType + "(Fail)";
  }


  public CommandResult call() throws Exception {
    // TODO Auto-generated method stub
    return null;
  }


  public CharBuffer asCharBuffer() {
    // TODO Auto-generated method stub
    return CharBuffer.wrap(notificationCommand());
  }


  public String getCommandName() {
    // TODO Auto-generated method stub
    return commandType;
  }


  public Integer getAckNumber() {
    // TODO Auto-generated method stub
    return null;
  }


  public ICommand wasAcked() {
    // TODO Auto-generated method stub
    return this;
  }


  public ICommand wasReceived() {
    // TODO Auto-generated method stub
    return this;
  }

  public Boolean wasSent() {
    return this.state == CommandState.SENT || this.state == CommandState.AWAITING_ACK || this.state == CommandState.DONE;
  }
  
  public ICommand setSent() {
    this.state = CommandState.SENT;
    return this;
  }

  public ICommand commandError() {
    // TODO Auto-generated method stub
    return null;
  }
}
