package tk.bad_rabbit.rcam.distributed_backend.command;

import java.nio.CharBuffer;

public class ErrorCommand implements ICommand {

  public CharBuffer asCharBuffer() {
    return CharBuffer.wrap("Error");
  }

  public CommandResult call() throws Exception {
    // TODO Auto-generated method stub
    return new CommandResult("Error");//"Error";
  }

}
