package tk.bad_rabbit.rcam.distributed_backend.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.state.CommandSentState;
import tk.bad_rabbit.rcam.distributed_backend.command.state.ErrorCommandState;
import tk.bad_rabbit.rcam.distributed_backend.command.state.ICommandState;
import tk.bad_rabbit.rcam.distributed_backend.command.state.ReceivedCommandState;
import tk.bad_rabbit.rcam.distributed_backend.commandfactory.ICommandFactory;
import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.IConfigurationProvider;
import tk.bad_rabbit.rcam.distributed_backend.controller.Controller;

public class ClientThread implements Runnable, Observer {
  int port;
  String address;
  
  Selector clientSelector;
  SocketChannel socketChannel;
  
  CharsetDecoder asciiDecoder;
  CharsetEncoder asciiEncoder;
  
  ICommandFactory commandFactory;
  IConfigurationProvider configurationProvider;
  Thread clientThread;
  List<Observer> observers;
  
  public ClientThread(String address, int port, ICommandFactory commandFactory, Controller controller) {
    this.port = port;
    this.address = address;
    this.commandFactory = commandFactory;
    
    observers = new ArrayList<Observer>();
    observers.add(controller);
    observers.add(this);
    

    this.asciiDecoder = Charset.forName("US-ASCII").newDecoder();
    this.asciiEncoder = Charset.forName("US-ASCII").newEncoder();
  }
  
  public void start() {
    this.clientThread = new Thread(this);
    clientThread.start();
  }
  
  public void run() {
    boolean running;
    System.out.println("RCam Distributed Backend - Trying to connect to " + address + ":" + port);
    try {
      connectToServer();
      running = true;
    } catch(IOException ioE) {
      running = false;
      ioE.printStackTrace();
    }
    
    while(running) {
      try {
        Thread.sleep(125);
        performPendingSocketIO();
      } catch (InterruptedException interrupted) {
        interrupted.printStackTrace();
      } catch(IOException ioE) {
        System.err.println("Ran into a critical error. Shutting down the server.");
        running = false;
      }
    }
    //shutdownServer();
  }
  
  public void connectToServer() throws IOException {
    clientSelector = Selector.open();
    socketChannel = SocketChannel.open();
    socketChannel.configureBlocking(false);
    socketChannel.connect(new InetSocketAddress(address, port));
    socketChannel.register(clientSelector, SelectionKey.OP_CONNECT);
  }
  
  private void performPendingSocketIO() throws IOException{
    if(clientSelector.select() == 0) { 
      return; 
    }
    
    Set<SelectionKey> selectedKeys = clientSelector.selectedKeys();
    Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
    while(keyIterator.hasNext()) {
      SelectionKey key = keyIterator.next();
      SocketChannel selectedChannel = (SocketChannel) key.channel();
      
      
      if(key.isConnectable()) {
        selectedChannel.finishConnect();
        selectedChannel.register(clientSelector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        //this.setState(new ConnectedClientState());
      }
      
      try {
        if(key.isReadable()) {
          //keyIterator.remove();
          List<CharBuffer> newCommands = readFromChannel(selectedChannel);
          for(CharBuffer cB : newCommands) {

            ACommand newCommand = commandFactory.createCommand(cB);
            if(newCommand != null) {
              
              newCommand.addObservers(observers);
              newCommand.setState(new ReceivedCommandState());
            }
          }
        }

      } catch(IOException ioException) {
        System.err.println("Client("+address + ":" + port+": Error reading from a channel. Closing that channel.");
        try {
          //this.setState(new DisconnectedClientState());
          selectedChannel.close();
        } catch (IOException e) {
          System.err.println("Error closing the channel.");
          e.printStackTrace();
        }
      }
    }
  }
  
  public List<CharBuffer> readFromChannel(SocketChannel selectedChannel) throws IOException {
    ArrayList<CharBuffer> returnedList = new ArrayList<CharBuffer>();
    
    String returnedBuffer;
    ByteBuffer buffer = ByteBuffer.allocate(1024);
    
    if(selectedChannel.read(buffer) == -1) {
      throw new IOException();  
    }
    buffer.flip();
    
    try {
      returnedBuffer = asciiDecoder.decode(buffer).toString();
    } catch (CharacterCodingException e) {
      e.printStackTrace();
      returnedBuffer =""; // CharBuffer.allocate(1024);
    }
    
    String[] tokens = returnedBuffer.split("\n");
    
    for(String commandString : tokens) {
      System.out.println(commandString);
      returnedList.add(CharBuffer.wrap(commandString));
    }
    
    return returnedList;
    
  }
  

  
  public void writeCommandToChannel(SocketChannel selectedChannel, ACommand command) throws IOException {
    System.out.println("RCam Distributed Backend - ClientThread - Sending " + command.getAckNumber());
    ByteBuffer buffer = asciiEncoder.encode(command.asCharBuffer());

    while(buffer.hasRemaining()) {
        selectedChannel.write(buffer);
    }    
    buffer.clear();
  }

  public void update(Observable o, Object arg) {
    ACommand updatedCommand = (ACommand) o;
    System.out.println("RCam Distributed Backend - ClientThread - Received an update for command " + updatedCommand.getAckNumber());
    updatedCommand.doNetworkAction(this);
  }
  
  public void sendAck(ACommand command) {
    System.out.println("RCam Distributed Backend - ClientThread - sendAck - Command " + command.getCommandName() + "[" + command.getAckNumber() + "] wants to send an ack");
    send(commandFactory.createAckCommand(command));
  }
  
  public void sendResult(ACommand command) {
    System.out.println("RCam Distributed Backend - ClientThread - sendResult - Command " + command.getCommandName() + "[" + command.getAckNumber() + "] wants to send its results");
    send(commandFactory.createResultCommand(command));
  }
  
  public void send(ACommand command) {
    System.out.println("RCam Distributed Backend - ClientThread - Trying to send "+  command.getCommandName() + "[" + command.getAckNumber()+"]");
    Iterator<SelectionKey> keyIterator = clientSelector.selectedKeys().iterator();
    while(keyIterator.hasNext()) {
      SelectionKey key = keyIterator.next();
      SocketChannel selectedChannel = (SocketChannel) key.channel();
      try {
        if(key.isWritable()) {
          writeCommandToChannel(selectedChannel, command);
          command.addObservers(observers);
          command.setState(new CommandSentState());
        } else {
          System.out.println("Key is not writable.");
        }
      } catch(IOException ioException) {
        System.err.println("Error reading from a channel. Closing that channel.");
        command.setState(new ErrorCommandState());
        try {
          selectedChannel.close();
        } catch (IOException e) {
          System.err.println("Error closing the channel.");
          e.printStackTrace();
        }
    }
  }
}
  
  /*
  public void delete_update(Observable observable, Object arg) {
    //synchronized(runController) {
      ACommand updatedCommand = (ACommand) observable;
      System.out.println("RCam Coordinator - ServerThread - Receieved an update for command " + updatedCommand.getAckNumber());
      Set<String> connectedClients = socketChannels.keySet();
      Iterator<String> i = connectedClients.iterator();
      while(i.hasNext()) {
        String rC = i.next();
        SocketChannel selectedChannel = socketChannels.get(rC);
      
        if(arg instanceof ICommandState) {
          updatedCommand.doNetworkAction(this, rC);
        }
      
        
        if(arg instanceof Map.Entry) {
          Map.Entry<ACommand, Map.Entry<String, ICommandState>> commandStateMap = (Map.Entry<ACommand, Map.Entry<String, ICommandState>> ) arg;
          if(rC.equals(commandStateMap.getValue().getKey())) {
            updatedCommand.doNetworkAction(this, rC);
          }  
        }
      }
  }
  */
  
}
