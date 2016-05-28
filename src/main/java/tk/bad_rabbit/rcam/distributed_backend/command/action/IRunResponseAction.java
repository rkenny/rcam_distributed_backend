package tk.bad_rabbit.rcam.distributed_backend.command.action;

import java.util.concurrent.Callable;

import tk.bad_rabbit.rcam.distributed_backend.controller.RunController;

public interface IRunResponseAction {
  public Callable<Integer> getRunCallable(RunController runController);
}
