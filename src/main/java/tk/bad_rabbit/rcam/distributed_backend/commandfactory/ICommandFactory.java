package tk.bad_rabbit.rcam.distributed_backend.commandfactory;

import java.nio.CharBuffer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.Pair;

public interface ICommandFactory {
  ACommand createCommand(CharBuffer commandBuffer);
  ACommand createCommand(String command);
  ACommand createAckCommand(ACommand incomingCommand);
  //ACommand createResultCommand(Pair<Integer, Integer> commandResults);
  ACommand createResultCommand(ACommand command);
}