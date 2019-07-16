package com.epam.hadoopadvanced.mapper;


import com.epam.hadoopadvanced.util.CommonConstant;
import org.apache.commons.cli.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

/**
 * The type Mapper driver.
 */
public class MapperDriver {

  private static final Logger LOGGER = LoggerFactory.getLogger(MapperDriver.class);

  private Options argsOptions;
  private String input;

  /**
   * The entry point of Mapper.
   *
   * @param args the input arguments
   */
  public static void main(String[] args) {
    MapperDriver mapperRunner = new MapperDriver();
    LOGGER.info("args: {}", Arrays.asList(args));

    try {
      if (mapperRunner.init(args)) {
        mapperRunner.runMapper();
      }
      else {
        mapperRunner.printUsage();
        System.exit(-1);
      }
    }
    catch (IOException e) {
      LOGGER.error("Exception occurs during mapper initialization: ", e);
      System.exit(1);
    }

  }

  /**
   * Instantiates a new Mapper driver.
   */
  public MapperDriver() {
    argsOptions = new Options();
    Option containersOpt = new Option("s", "start_line", true, "The file line to start from");
    containersOpt.setRequired(true);
    containersOpt.setArgName("n");
    argsOptions.addOption(containersOpt);

    Option indexOpt = new Option("e", "end_line", true, "The file line where to stop");
    indexOpt.setRequired(true);
    indexOpt.setArgName("n");
    argsOptions.addOption(indexOpt);

    Option inputOpt = new Option("i", "input", true, "The file to process");
    inputOpt.setRequired(true);
    inputOpt.setArgName("path");
    argsOptions.addOption(inputOpt);

    Option helpOpt = new Option("h", "help", false, "Prints this help");
    argsOptions.addOption(helpOpt);
  }

  /**
   * Init boolean.
   *
   * @param args the args
   * @return the boolean
   */
  public boolean init(String[] args) {
    boolean isInit = true;

    CommandLine cliParser = null;
    try {
      cliParser = new GnuParser().parse(argsOptions, args);
    }
    catch (ParseException e) {
      LOGGER.error("Options parse exception", e);
      isInit = false;
    }
    
    if (cliParser != null) {
      if( cliParser.hasOption("h")){
        isInit = false;  
      }
      input = cliParser.getOptionValue("i");
    }

    return isInit;
  }

  private void printUsage() {
    new HelpFormatter().printHelp("All available arguments: ", argsOptions);
  }

  /**
   * Reads data from input file, saves result into temporary file.
   *
   * @throws IOException if input file cannot be opened.
   */
  private void runMapper() throws IOException {
    LOGGER.info("runMapper");

    FileSystem fs = FileSystem.get(new Configuration());
    Path path = new Path(input);
    String tempDir = CommonConstant.TEMP_DIR_NAME + CommonConstant.TEMP_FILE_NAME;
    LOGGER.info("writing to {}", tempDir);

    try (FSDataInputStream in = fs.open(path);
        FSDataOutputStream out = fs.create(new Path(tempDir));
        InputStreamReader inReader = new InputStreamReader(in);
        BufferedReader bufferedReader = new BufferedReader(inReader)) {

      HotelMapper mapper = new HotelMapper();
      Map<String, Long> result = mapper.map(bufferedReader.lines());

      for (Entry<String, Long> entry : result.entrySet()) {
        out.writeBytes(entry.getKey() + CommonConstant.KEY_VALUE_SEPARATOR
                + entry.getValue() + CommonConstant.RECORD_SEPARATOR);
      }

    }

  }

}
