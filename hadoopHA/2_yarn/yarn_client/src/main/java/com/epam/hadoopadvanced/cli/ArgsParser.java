package com.epam.hadoopadvanced.cli;

import com.epam.hadoopadvanced.service.HelpService;
import com.epam.hadoopadvanced.service.Service;
import com.epam.hadoopadvanced.service.YarnClientService;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class ArgsParser {

  public static final String SYNTAX = String
      .format("yarn-submit %s <app_jar> <file> [args]", System.getProperty("jar.name"));
  public static final String HEADER = "Launches java mapReduce-like application on YARN. "
      + "Application jar locate on the distributed file system.";

  public static final String DEFAULT_NAME = "hadoop_advanced_hw";
  public static final String DEFAULT_QUEUE = "default";
  public static final int DEFAULT_PARALLELISM = 1;

  private Options options;

  public ArgsParser() {
    options = new Options();
    options.addOption(new Option("h", "help", false, "Shows this help"));
    Option nameOpt = new Option("n", "name", true,
        String.format("Specifies YARN application's name. Default is \"%s\"", DEFAULT_NAME));
    nameOpt.setArgName("app_name");
    options.addOption(nameOpt);
    Option queueOpt = new Option("q", "queue", true,
        String.format("Specifies application's submit queue. Default is \"%s\"", DEFAULT_QUEUE));
    queueOpt.setArgName("queue_name");
    options.addOption(queueOpt);
    Option parallelOpt = new Option("p", "parallelism", true,
        "Specifies number of containers to create for application map phase. Default is "
            + DEFAULT_PARALLELISM);
    parallelOpt.setArgName("n_threads");
    options.addOption(parallelOpt);
    Option mapperOpt = new Option("m", "mapper", true, "Mapper class to run");
    mapperOpt.setRequired(true);
    mapperOpt.setArgName("mapper_class");
    options.addOption(mapperOpt);
    Option reducerOpt = new Option("r", "reducer", true, "Reducer class to run");
    reducerOpt.setRequired(true);
    reducerOpt.setArgName("reducer_class");
    options.addOption(reducerOpt);
    Option inputOpt = new Option("i", "inputFile", true, "Input file path");
    inputOpt.setRequired(true);
    inputOpt.setArgName("path");
    options.addOption(inputOpt);
    Option outputOpt = new Option("o", "outputFile", true, "Path where result will be stored");
    outputOpt.setRequired(true);
    outputOpt.setArgName("path");
    options.addOption(outputOpt);

  }

  public Service parseArgs(String[] args) {
    try {
      CommandLine cli = new GnuParser().parse(options, args);
      if (cli.hasOption("h") || cli.getArgList().isEmpty()) {
        return new HelpService(SYNTAX, HEADER, options);
      }
      return initYarnClientService(cli);
    } catch (ParseException e) {
      return new HelpService(String.format("%s%n%s", e.getMessage(), SYNTAX), HEADER, options);
    }
  }

  private Service initYarnClientService(CommandLine cli) {
    YarnClientService service = new YarnClientService();
    service.setAppName(cli.getOptionValue("n", DEFAULT_NAME));
    service.setQueue(cli.getOptionValue("q", DEFAULT_QUEUE));
    int nContainers;
    try {
      nContainers = Integer.parseInt(cli.getOptionValue("p"));
      if (nContainers < 1) {
        nContainers = DEFAULT_PARALLELISM;
      }
    } catch (NullPointerException | NumberFormatException e) {
      nContainers = DEFAULT_PARALLELISM;
    }
    service.setParallelism(nContainers);
    service.setMapperClass(cli.getOptionValue("m"));
    service.setReducerClass(cli.getOptionValue("r"));
    service.setInputPath(cli.getOptionValue("i"));
    service.setOutputPath(cli.getOptionValue("o"));
    List<String> args = cli.getArgList();
    service.setAppJar(args.get(0));
    args.remove(0);
    service.setJarArgs(args);
    return service;
  }

}
