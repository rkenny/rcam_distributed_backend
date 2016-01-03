package tk.bad_rabbit.rcam.distributed_backend.command.responseactions;

import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.controller.Controller;

public class AckCommandResponseAction extends ACommandResponseAction {

 @Override
 public void doStuff(Observer actionObject, ACommand actionSubject) {}
 
 public void nextState(ACommand command) {}
 
 
}
