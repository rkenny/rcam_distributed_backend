package tk.bad_rabbit.rcam.distributed_backend.commandfactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.CharBuffer;

import tk.bad_rabbit.rcam.distributed_backend.command.ICommand;
import tk.bad_rabbit.rcam.distributed_backend.command.Test;

public class CommandFactory implements ICommandFactory {

  public ICommand createCommand(CharBuffer commandCharBuffer) {
    ICommand command = null;
    Class<?> commandClass;
    try {
      commandClass = Class.forName("tk.bad_rabbit.rcam.distributed_backend.command." + commandCharBuffer.toString().trim());
      Constructor<?> commandConstructor = commandClass.getConstructor();
      command = (ICommand) commandConstructor.newInstance();
    } catch (ClassNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (InstantiationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IllegalArgumentException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (SecurityException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
  
    return command;
  }

  public ICommand ackCommand() {
    // TODO Auto-generated method stub
    return new Test();
  }
  
  public ICommand errorCommand() {
    return new Test();
  }

}
