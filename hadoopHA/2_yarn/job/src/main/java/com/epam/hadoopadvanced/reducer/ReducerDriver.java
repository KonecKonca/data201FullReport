package com.epam.hadoopadvanced.reducer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.epam.hadoopadvanced.util.CommonConstant;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The type Reducer driver.
 */
public class ReducerDriver {

  private static final Logger LOGGER = LoggerFactory.getLogger(ReducerDriver.class);

  private Options options;
  private String outputPath;

  /**
   * The entry point of Reducer.
   *
   * @param args the input arguments
   */
  public static void main(String[] args) {
    ReducerDriver reducerDriver = new ReducerDriver();
    LOGGER.info("Initializing Reducer");
  
    try {
      if (reducerDriver.init(args)) {
        reducerDriver.runReducer();
      } 
      else {
        reducerDriver.printUsage();
        System.exit(-1);
      }
    } 
    catch (IOException e) {
      LOGGER.error("Exception occurs during reducer initialization: " + e.getMessage(), e);
      System.exit(1);
    }
  
  }

  /**
   * Instantiates a new Reducer driver.
   */
  public ReducerDriver() {
    options = new Options();

    Option outputOpt = new Option("o", "output", true, "The path where result will be stored");
    outputOpt.setRequired(true);
    outputOpt.setArgName("path");
    options.addOption(outputOpt);

    Option helpOpt = new Option("h", "help", false, "Prints this help");
    options.addOption(helpOpt);
  }

  private void printUsage() {
    new HelpFormatter().printHelp("Run reducer application", options);
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
      cliParser = new GnuParser().parse(options, args);
    } 
    catch (ParseException e) {
      LOGGER.error(e.getMessage(), e);
      isInit = false;
    }
    
    if(cliParser != null){
      if(cliParser.hasOption("h")){
        isInit =  false;
      }
      
      outputPath = cliParser.getOptionValue("o");
    }
    
    return isInit;
  }

  /**
   * Runs {@link HotelReducer} into the stream of combined mapper file. Writes the reducer result to
   * the output file and clears temp data.
   */
  private void runReducer() throws IOException {
    FileSystem fs = FileSystem.get(new Configuration());
    try (InputStream sequenceStream = aggregatesMapperFiles(fs);
        FSDataOutputStream out = fs.create(new Path(outputPath), true);
        InputStreamReader inReader = new InputStreamReader(sequenceStream);
        BufferedReader bufferedReader = new BufferedReader(inReader)) {
      HotelReducer reducer = new HotelReducer();

      Map<String, Long> result = reducer.reduce(bufferedReader.lines());
      for (Entry<String, Long> entry : result.entrySet()) {
        out.writeBytes(entry.getKey() + CommonConstant.KEY_VALUE_SEPARATOR 
                + entry.getValue() + CommonConstant.RECORD_SEPARATOR);
      }

    }
    
    fs.delete(new Path(CommonConstant.TEMP_DIR_NAME), true);
  }


  /**
   * Aggregates files produced into the single
   * input stream
   *
   * @param fs the underlying file system
   * @return the input stream for all mapper files
   * @throws IOException if mapper file cannot be accessed.
   */
  private InputStream aggregatesMapperFiles(FileSystem fs) throws IOException {
    FileStatus[] inputs = fs.globStatus(new Path(CommonConstant.TEMP_DIR_NAME + CommonConstant.TEMP_FILE_NAME + "*"));
    
    List<InputStream> inputStreams = new ArrayList<>(inputs.length);
    for (FileStatus input : inputs) {
      InputStream in = fs.open(input.getPath());
      inputStreams.add(in);
    }
    
    return new SequenceInputStream(Collections.enumeration(inputStreams));
  }

}
