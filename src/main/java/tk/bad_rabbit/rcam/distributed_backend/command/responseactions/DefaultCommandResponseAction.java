package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.Observer;
import java.util.concurrent.Future;

import org.apache.commons.lang3.concurrent.ConcurrentUtils;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;

public class DefaultCommandResponseAction extends ACommandResponseAction {
  @Override
  public Future doStuff(Observer actionObject, ACommand actionSubject) {
    System.out.println("DefaultCommandResponseAction doing nothing (doStuff)");
    return ConcurrentUtils.constantFuture(true);
  }
  

}


