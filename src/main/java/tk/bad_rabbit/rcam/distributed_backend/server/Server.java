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
import java.util.Collection;
import java.util.Iterator;

import tk.bad_rabbit.rcam.distributed_backend.command.ICommand;
import tk.bad_rabbit.rcam.distributed_backend.commandfactory.CommandFactory;
import tk.bad_rabbit.rcam.distributed_backend.commandfactory.ICommandFactory;
import tk.bad_rabbit.rcam.distributed_backend.commandqueue.ICommandQueuer;
import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.IConfigurationProvider;

public class Server implements Runnable {
  int port;
  
  ServerSocketChannel serverSocketChannel;
  SocketChannel socketChannel;
  Selector serverSelector;
  
  CharsetDecoder asciiDecoder;
  CharsetEncoder asciiEncoder;
  
  ICommandQueuer commandQueuer;
  ICommandFactory commandFactory;
  IConfigurationProvider configurationProvider;
  
  public Server(int port, ICommandQueuer commandQueuer, IConfigurationProvider configurationProvider) {
    this();
    this.port = port;
    this.commandQueuer = commandQueuer;
    this.configurationProvider = configurationProvider;
    this.commandFactory = new CommandFactory(this.configurationProvider.getCommandConfigurations(), 
        this.configurationProvider.getCommandVariables(), this.configurationProvider.getServerVariables());
    
  }
  
  public Server() {
    asciiDecoder = Charset.forName("US-ASCII").newDecoder();
    asciiEncoder = Charset.forName("US-ASCII").newEncoder();
  }

  public void run() {
    boolean running;
    System.out.println("Trying to start server on port " + port);
    try {
      initializeSelectors();
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
  
  private void initializeSelectors() throws IOException{
    
    serverSelector = Selector.open();
    serverSocketChannel = ServerSocketChannel.open();
    serverSocketChannel.configureBlocking(false);
    serverSocketChannel.socket().bind(new InetSocketAddress(port));
    serverSocketChannel.register(serverSelector, SelectionKey.OP_ACCEPT);
    
    
  }
  
  private void performPendingSocketIOs() throws IOException {
    if(socketChannel != null) {
      if(serverSelector.select() == 0) {
        return;
      }
      
      Iterator<SelectionKey> selectedKeyIterator = serverSelector.selectedKeys().iterator();
      while(selectedKeyIterator.hasNext()) {
        SelectionKey selectedKey = selectedKeyIterator.next();
        selectedKeyIterator.remove();
        SocketChannel selectedChannel = (SocketChannel) selectedKey.channel();
        
        
        if(!selectedKey.isValid() || !selectedChannel.isConnected()) {
          System.out.println("selected key not valid or channel isn't connected");
          commandQueuer.flushQueues();
          selectedChannel.close();
          continue;
        }
        
        if(selectedKey.isReadable()) {
          try {
            ICommand incomingCommand = commandFactory.createCommand(readFromChannel(selectedChannel));
            if(null != incomingCommand) {
              commandQueuer.addIncomingCommand(incomingCommand.wasReceived());
              if(!incomingCommand.isIgnored()) {
                // the command should set its state to ACKED later on, but that requires bigger changes.
                writeCommandToChannel(selectedChannel, commandFactory.createAckCommand(incomingCommand.wasAcked()));                
              }
              
              incomingCommand.setReadyToExecute();
            } else {
              writeCommandToChannel(selectedChannel, commandFactory.createCommand("Error"));  
            }
            
            
          }  catch(IOException ioException) {
            System.err.println("Error reading from a channel. Closing that channel.");
            try {
              commandQueuer.flushQueues();
              selectedChannel.close();
            } catch (IOException e) {
              System.err.println("Error closing the channel.");
              e.printStackTrace();
              continue;
            }
            continue;
          }
        }
        if(selectedKey.isWritable()) {
          try {
            ICommand outgoingCommand;
            while((outgoingCommand = commandQueuer.getNextReadyToSendOutgoingCommand()) != null) {
              writeCommandToChannel(selectedChannel, outgoingCommand.setSent());
            }
          } catch(IOException ioException) {
            System.err.println("Error writing to a channel. Closing that channel");
            ioException.printStackTrace();
            try {
              commandQueuer.flushQueues();
              socketChannel.register(serverSelector, SelectionKey.OP_READ);
              selectedChannel.close();
            } catch (IOException e) {
              System.out.println("Error closing that channel");
              e.printStackTrace();
              continue;
            }
            continue;
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
    System.out.println("Wrote " + asciiEncoder.encode(command.asCharBuffer()).toString());
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
        System.out.println("Nothing to select, yet");
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
