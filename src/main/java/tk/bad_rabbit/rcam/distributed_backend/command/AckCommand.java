package tk.bad_rabbit.rcam.distributed_backend.command;

import java.nio.CharBuffer;

public class AckCommand implements ICommand {
  public CharBuffer asCharBuffer() {    
    return CharBuffer.wrap("Ack");
  }

  public CommandResult call() throws Exception {
    // TODO Auto-generated method stub
    return new CommandResult("Ack").setSuccess();
  }

}
