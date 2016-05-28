package tk.bad_rabbit.rcam.distributed_backend.command.action;

import java.util.concurrent.Callable;

import tk.bad_rabbit.rcam.distributed_backend.client.ClientThread;

public interface INetworkAction {
  public Callable<Integer> getNetworkCallable(ClientThread clientThread);
}
