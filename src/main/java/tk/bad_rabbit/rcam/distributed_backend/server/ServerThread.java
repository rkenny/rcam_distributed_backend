package tk.bad_rabbit.rcam.distributed_backend.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

import tk.bad_rabbit.rcam.distributed_backend.command.ACommand;
import tk.bad_rabbit.rcam.distributed_backend.command.ICommand;
import tk.bad_rabbit.rcam.distributed_backend.command.state.AckedState;
import tk.bad_rabbit.rcam.distributed_backend.command.state.ErrorCommandState;
import tk.bad_rabbit.rcam.distributed_backend.command.state.ICommandState;
import tk.bad_rabbit.rcam.distributed_backend.command.state.ReceivedCommandState;
import tk.bad_rabbit.rcam.distributed_backend.commandfactory.ICommandFactory;
import tk.bad_rabbit.rcam.distributed_backend.controller.Controller;

public class ServerThread implements Runnable, Observer {
  
  ServerSocketChannel serverSocketChannel;
  SocketChannel socketChannel;
  Selector serverSelector;
  
  int port;
  
  CharsetDecoder asciiDecoder;
  CharsetEncoder asciiEncoder;
  public Thread serverThread;
  ICommandFactory commandFactory;
  Controller controller;
  
  public void observeCommand(ACommand command) {
      System.out.println("Server Observing " + command.getAckNumber());
      command.addObserver(this);
  }
  
  public void update(Observable updatedCommand, Object arg) {
    System.out.println("ServerThread received a notification about " + ((ACommand) updatedCommand).getAckNumber());
    System.out.println("Its new state is " + arg.getClass().getSimpleName());
    
    
    ((ACommand) updatedCommand).doAction(this, (ICommandState) arg); 
  }
  
  

  
  
  public ServerThread(ICommandFactory commandFactory, Controller controller, int port) {
    this.port = port;
    this.commandFactory = commandFactory;
    this.controller = controller;
    

    this.asciiDecoder = Charset.forName("US-ASCII").newDecoder();
    this.asciiEncoder = Charset.forName("US-ASCII").newEncoder();
  }
  
  public void start() {
    this.serverThread = new Thread(this);
    serverThread.start();
  }
  
  
  
  public void run() {
    boolean running;
    System.out.println("Trying to start server on port " + port);
    try {
      initializeServer();
      running = true;
    } catch(IOException ioE) {
      running = false;
      ioE.printStackTrace();
    }
    
    while(running) {
      try {
        Thread.sleep(125);
      } catch (InterruptedException interrupted) {
        interrupted.printStackTrace();
      }
      try {
        acceptPendingConnections();
        performPendingSocketIOs();
      } catch(IOException ioE) {
        System.err.println("Ran into a critical error. Shutting down the server.");
        running = false;
      }
    }
    shutdownServer();
  }

  private void shutdownServer() {
    try {
      serverSocketChannel.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  private void initializeServer() throws IOException{
    
    serverSelector = Selector.open();
    serverSocketChannel = ServerSocketChannel.open();
    serverSocketChannel.configureBlocking(false);
    serverSocketChannel.socket().bind(new InetSocketAddress(port));
    serverSocketChannel.register(serverSelector, SelectionKey.OP_ACCEPT);
  }
  
  public void sendAck(ACommand command) {
      ACommand ackCommand = commandFactory.createAckCommand(command);
      send(ackCommand);
      //observeCommand(ackCommand);
      ICommandState ackedState = new AckedState();
      System.out.println("Send Ack called.");
      command.setState(ackedState);
      //command.notifyObservers(ackedState);
  }
  
  public void send(ACommand command) {
      Iterator<SelectionKey> keyIterator = serverSelector.selectedKeys().iterator();
      while(keyIterator.hasNext()) {
        SelectionKey key = keyIterator.next();
        SocketChannel selectedChannel = (SocketChannel) key.channel();
        try {
          if(key.isWritable()) {
            //keyIterator.remove();
            writeCommandToChannel(selectedChannel, command);
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
  
  private void performPendingSocketIOs() throws IOException {
    if(socketChannel != null) {
      if(serverSelector.select() == 0) {
        return;
      }
      Iterator<SelectionKey> selectedKeyIterator = serverSelector.selectedKeys().iterator();
      while(selectedKeyIterator.hasNext()) {
        SelectionKey selectedKey = selectedKeyIterator.next();
        //selectedKeyIterator.remove();
        
        SocketChannel selectedChannel = (SocketChannel) selectedKey.channel();
        if(!selectedKey.isValid() || !selectedChannel.isConnected()) {
          System.out.println("selected key not valid or channel isn't connected");
          selectedChannel.close();
        }
        
        if(selectedKey.isReadable()) {
          //selectedKeyIterator.remove();
          try {
          ACommand incomingCommand = commandFactory.createCommand(readFromChannel(selectedChannel));
          controller.observeCommand(incomingCommand);
          this.observeCommand(incomingCommand);
          ICommandState newState = new ReceivedCommandState();
          incomingCommand.setState(newState);
          } catch(IOException ioException) {
            System.err.println("Error reading from a channel. Closing that channel.");
            try {
              selectedChannel.close();
            } catch (IOException e) {
              System.err.println("Error closing the channel.");
              e.printStackTrace();
            }
            
          } 
        }
      }
    }
    return;
  }
      
  
  
  public void writeCommandToChannel(SocketChannel selectedChannel, ICommand command) throws IOException {
    ByteBuffer buffer = asciiEncoder.encode(command.asCharBuffer());

    while(buffer.hasRemaining()) {
        selectedChannel.write(buffer);
    }
    System.out.println("Wrote " + command.asCharBuffer().toString());
    buffer.clear();
  }
  
  public CharBuffer readFromChannel(SocketChannel selectedChannel) throws IOException {
    CharBuffer returnedBuffer;
    ByteBuffer buffer = ByteBuffer.allocate(1024);
  
    selectedChannel.read(buffer);
    buffer.flip();
    
    try {
      returnedBuffer = asciiDecoder.decode(buffer);
    } catch (CharacterCodingException e) {
      e.printStackTrace();
      returnedBuffer = CharBuffer.allocate(1024);
    }
    
    return returnedBuffer;
    
  }
  
  private void acceptPendingConnections() throws IOException {
    
    if(serverSocketChannel != null) {
      if(serverSelector.select() == 0) {
        //System.out.println("Nothing to select, yet");
        return;
      }
    }
    
    Iterator<SelectionKey> serverKeyIterator = serverSelector.selectedKeys().iterator();
    while(serverKeyIterator.hasNext()) {
      SelectionKey selectedKey = serverKeyIterator.next();
      serverKeyIterator.remove();
      if(!selectedKey.isValid()) {
        continue;
      }
      if(selectedKey.isAcceptable()) {
        System.out.println("Accepting a new connection.");
        socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(serverSelector, SelectionKey.OP_WRITE | SelectionKey.OP_READ);
      }
    }
  }

}
