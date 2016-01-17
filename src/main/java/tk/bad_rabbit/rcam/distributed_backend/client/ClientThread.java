package tk.bad_rabbit.rcam.distributed_backend.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.CancelledKeyException;
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
import tk.bad_rabbit.rcam.distributed_backend.command.state.ReceivedCommandState;
import tk.bad_rabbit.rcam.distributed_backend.commandfactory.ICommandFactory;
import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.ConfigurationProvider;
import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.IConfigurationProvider;
import tk.bad_rabbit.rcam.distributed_backend.controller.Controller;

public class ClientThread extends Observable implements Runnable, Observer {
  //int port;
  //String address;
  
  Selector clientSelector;
  SocketChannel socketChannel;
  
  CharsetDecoder asciiDecoder;
  CharsetEncoder asciiEncoder;
  
  ICommandFactory commandFactory;
  IConfigurationProvider configurationProvider;
  
  Thread clientThread;
  List<Observer> observers;
  
  public ClientThread(IConfigurationProvider configurationProvider, ICommandFactory commandFactory, Controller controller) {
    
    //this.address = (String) configurationProvider.getServerVariable("address");
    
    this.commandFactory = commandFactory;
    this.configurationProvider = configurationProvider;
    
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
    System.out.println("RCam Distributed Backend - Trying to connect.");
    try {
      connectToServer();
      running = true;
    } catch(IOException ioE) {
      running = false;
      ioE.printStackTrace();
    }
    
    while(running) {
      try {
        Thread.sleep(1250);
        finishPendingConnections();
        performPendingSocketIO();
      
      } catch (InterruptedException interrupted) {
        interrupted.printStackTrace();
      } catch(IOException ioE) {
        System.err.println("Ran into a critical error. Shutting down the server.");
        running = false;
      }
      
      pollConnectedSockets();
      
    }
    //shutdownServer();
  }
  

  private void pollConnectedSockets()  {
    CharBuffer buffer = CharBuffer.wrap("!\n");
    
    Set<SelectionKey> selectedKeys = clientSelector.selectedKeys();
    Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
    while(keyIterator.hasNext()) {
      SelectionKey key = keyIterator.next();
      SocketChannel selectedChannel = (SocketChannel) key.channel();
      
      SelectionKey selectedKey = selectedChannel.keyFor(clientSelector);
      
      if(selectedKey.isValid() && selectedKey.isWritable()) {
        try {
          writeToChannel(selectedChannel, buffer);
        } catch(IOException e) {
          closeSocket(selectedChannel);
        } 
      }
    }
  }

  private void closeSocket(SocketChannel socketChannel) {
    try {
      socketChannel.close();
    } catch(IOException e) {
      e.printStackTrace();
    }
    
    setChanged();
    notifyObservers("remoteAddress");
  }
  
  
  public void connectToServer() throws IOException {
    clientSelector = Selector.open();
    socketChannel = SocketChannel.open();
    socketChannel.configureBlocking(false);
    socketChannel.connect(new InetSocketAddress((String) configurationProvider.getServerVariable("remoteAddress"), (Integer) configurationProvider.getServerVariable("remotePort")));
    socketChannel.register(clientSelector, SelectionKey.OP_CONNECT);
    
    
    
  }
  
  private void finishPendingConnections() throws IOException {
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
        
        System.out.println("RCam Distributed Backend - Client Connected.");
        System.out.println("RCam Distributed Backend - Local Address "+ selectedChannel.getLocalAddress().toString().substring(1));
        System.out.println("RCam Distributed Backend - Remote Address:" + selectedChannel.getRemoteAddress().toString().substring(1));
        Integer cI = selectedChannel.getLocalAddress().toString().indexOf(":");
        this.configurationProvider.setServerVariable("localAddress", selectedChannel.getLocalAddress().toString().substring(1, cI) );
        this.configurationProvider.setServerVariable("localPort", selectedChannel.getLocalAddress().toString().substring(cI+1) );
      }
      
      
    }
    
  }
  
  private void performPendingSocketIO(){
    try {
      clientSelector.select();
    } catch(IOException e) {
      e.printStackTrace();
      return;
    }

    
    Set<SelectionKey> selectedKeys = clientSelector.selectedKeys();
    Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
    while(keyIterator.hasNext()) {
      SelectionKey key = keyIterator.next();
      SocketChannel selectedChannel = (SocketChannel) key.channel();
      
      
      
      
      try {
        if(key.isValid() && key.isReadable()) {
          
          List<CharBuffer> newCommands = readFromChannel(selectedChannel);
          for(CharBuffer cB : newCommands) {
            if(cB.toString().length() > 3) {
              ACommand newCommand = commandFactory.createCommand(cB);
              if(newCommand != null) {
                
                newCommand.addObservers(observers);
                newCommand.setState(new ReceivedCommandState());
              }
            }
          }
        }

      } catch(IOException ioException) {
        System.err.println("Client: Error reading from a channel. Closing that channel.");
        closeSocket(selectedChannel);
      }
    }
  }
  
  public List<CharBuffer> readFromChannel(SocketChannel selectedChannel) throws IOException {
    ArrayList<CharBuffer> returnedList = new ArrayList<CharBuffer>();
    
    String returnedBuffer;
    ByteBuffer buffer = ByteBuffer.allocate(1024);
    

    selectedChannel.read(buffer);
    buffer.flip();
    
    try {
      returnedBuffer = asciiDecoder.decode(buffer).toString();
    } catch (CharacterCodingException e) {
      e.printStackTrace();
      returnedBuffer ="";
    }
    
    buffer.clear();
    
    String[] tokens = returnedBuffer.split("\n");
    
    for(String commandString : tokens) {
      returnedList.add(CharBuffer.wrap(commandString));
    }
    
    return returnedList;
    
  }
  

  
  public void writeCommandToChannel(SocketChannel selectedChannel, ACommand command) throws IOException  {
    writeToChannel(selectedChannel, command.asCharBuffer());
  }
  
  public void writeToChannel(SocketChannel selectedChannel, CharBuffer buffer) throws IOException {

    while(buffer.hasRemaining()) {
        selectedChannel.write(asciiEncoder.encode(buffer));
    }    
    buffer.clear();

  }
  

  public void update(Observable o, Object arg) {
    ACommand updatedCommand = (ACommand) o;
    updatedCommand.doNetworkAction(this);
  }
  
  public void sendAck(ACommand command) {
    send(commandFactory.createAckCommand(command));
  }
  
  public void sendResult(ACommand command) {
    send(commandFactory.createResultCommand(command));
  }
  
  public void send(ACommand command) {
    Iterator<SelectionKey> keyIterator = clientSelector.selectedKeys().iterator();
    while(keyIterator.hasNext()) {
      SelectionKey key = keyIterator.next();
      SocketChannel selectedChannel = (SocketChannel) key.channel();
      try {
        if(key.isValid() && key.isWritable()) {
          writeCommandToChannel(selectedChannel, command);
          //command.addObservers(observers);
          //command.setState(new CommandSentState());
        } else {
          System.out.println("Key is not writable.");
        }
      } catch(IOException ioException) {
        System.err.println("Error reading from a channel. Closing that channel.");
        command.setState(new ErrorCommandState());
        
        closeSocket(selectedChannel);
        
      }
    }
  }
  
}
