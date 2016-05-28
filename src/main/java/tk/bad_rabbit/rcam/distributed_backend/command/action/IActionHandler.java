package tk.bad_rabbit.rcam.distributed_backend.command.action;

import java.util.concurrent.Future;

public interface IActionHandler {
  public Future<Integer> handleAction(ICommandAction action);
}
