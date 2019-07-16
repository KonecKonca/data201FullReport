package com.epam.hadoopadvanced;

import com.epam.hadoopadvanced.cli.ArgsParser;
import java.io.IOException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Runner {

  private static final Logger LOGGER = LoggerFactory.getLogger(Runner.class);

  public static void main(String[] args) {
    try {
      bootstrapProperties();
      new ArgsParser().parseArgs(args).execute();
    } catch (IOException e) {
      LOGGER.error("Could not load application properties.");
    }
  }

  /**
   * Load properties created by maven at compilation time
   *
   * @throws IOException if no property file exists due to wrong compilation or removing
   */
  private static void bootstrapProperties() throws IOException {
    Properties properties = new Properties();
    properties.load(Runner.class.getResourceAsStream("/application.properties"));
    for (String key : properties.stringPropertyNames()) {
      System.setProperty(key, properties.getProperty(key));
    }
  }
}
