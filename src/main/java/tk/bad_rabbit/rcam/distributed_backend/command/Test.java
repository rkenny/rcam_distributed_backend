package tk.bad_rabbit.rcam.distributed_backend.command;

import java.nio.CharBuffer;

public class Test implements ICommand {
  public void run() {
    System.out.println("This is the output you are looking for.");
  }

  public CharBuffer asByteBuffer() {
    
    return CharBuffer.wrap("This is the output you are looking for as a byteBuffer");
  }
}
