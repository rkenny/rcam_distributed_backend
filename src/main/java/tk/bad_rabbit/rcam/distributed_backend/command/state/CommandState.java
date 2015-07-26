package tk.bad_rabbit.rcam.distributed_backend.command.state;

public enum CommandState {
  NEW, AWAITING_ACK, ACKED, READY_TO_SEND, SENT, DONE, ERROR, RECEIVED
}
