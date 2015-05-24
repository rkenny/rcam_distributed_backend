package tk.bad_rabbit.rcam.distributed_backend.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tk.bad_rabbit.rcam.distributed_backend.configurationprovider.IConfigurationProvider;

public class ConfigurationProvider implements IConfigurationProvider {
  Map<String, List<String>> commandConfigurations;
  int serverPort;
  
  public ConfigurationProvider() {
    readServerConfiguration();
    readCommandConfigurations();
   
  }
  
  private void readServerConfiguration() {
    serverPort = 12345;
  }
  
  private void readCommandConfigurations() {
    commandConfigurations = new HashMap<String, List<String>>();
    
    File commandConfigFolder = new File("config/commands");
    for(File commandConfigFile : commandConfigFolder.listFiles()) {
      System.out.println("File: " + commandConfigFile.getAbsolutePath());
      List<String> commandArgs = new ArrayList<String>();
      if(commandConfigFile.isFile()) {

        BufferedReader reader;
        try {
          reader = new BufferedReader(new FileReader(commandConfigFile));
          String configFileLine;
          while((configFileLine = reader.readLine()) != null) {
            commandArgs.add(configFileLine);
          }
          reader.close();
          commandConfigurations.put(commandConfigFile.getName(), commandArgs);
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }     
    }

  //args.add(duration.toString());
  //args.add(videoSource);
  }
  
  public int getServerPort() {
    return serverPort;
  }

  public Map<String, List<String>> getCommandConfigurations() {
    return commandConfigurations;
  }
  

}
