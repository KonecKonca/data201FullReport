package com.epam.hadoopadvanced.cli;

import com.epam.hadoopadvanced.service.HelpService;
import com.epam.hadoopadvanced.service.Service;
import com.epam.hadoopadvanced.service.YarnClientService;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class ArgsParserTest {

  private ArgsParser parser = new ArgsParser();

  @Test
  public void parseValidArgs() {
    String[] args = {"-n", "name", "-q", "queue", "-p", "5", "-m", "mapper", "-r", "reducer", "-i",
        "inputFile", "-o", "outputFile", "jarFile", "jarArg1", "jarArg2"};
    YarnClientService service = new YarnClientService();
    service.setAppName("name");
    service.setQueue("queue");
    service.setParallelism(5);
    service.setMapperClass("mapper");
    service.setReducerClass("reducer");
    service.setInputPath("inputFile");
    service.setOutputPath("outputFile");
    service.setAppJar("jarFile");
    List<String> jarArgs = new ArrayList<>(2);
    jarArgs.add("jarArg1");
    jarArgs.add("jarArg2");
    service.setJarArgs(jarArgs);
    Assert.assertEquals(service, parser.parseArgs(args));
  }

  @Test
  public void parseDefaultArgs() {
    String[] args = {"-m", "mapper", "-r", "reducer", "-i", "inputFile", "-o", "outputFile",
        "jarFile", "jarArg1", "jarArg2"};
    YarnClientService service = new YarnClientService();
    service.setAppName(ArgsParser.DEFAULT_NAME);
    service.setQueue(ArgsParser.DEFAULT_QUEUE);
    service.setParallelism(ArgsParser.DEFAULT_PARALLELISM);
    service.setMapperClass("mapper");
    service.setReducerClass("reducer");
    service.setInputPath("inputFile");
    service.setOutputPath("outputFile");
    service.setAppJar("jarFile");
    List<String> jarArgs = new ArrayList<>(2);
    jarArgs.add("jarArg1");
    jarArgs.add("jarArg2");
    service.setJarArgs(jarArgs);
    Assert.assertEquals(service, parser.parseArgs(args));
  }

  @Test
  public void parseInValidArgs() {
    String[] args = {"--invalid", "name", "-q", "queue", "-p", "5", "-m", "mapper", "-r", "reducer",
        "-i", "inputFile", "-o", "outputFile", "jarFile", "jarArg1", "jarArg2"};
    Service service = parser.parseArgs(args);
    Assert.assertTrue(service instanceof HelpService);
  }

  @Test
  public void parseRequiredMissingArgs() {
    String[] args = {"-r", "reducer", "-i", "inputFile", "-o", "outputFile", "jarFile", "jarArg1",
        "jarArg2"};
    Service service = parser.parseArgs(args);
    Assert.assertTrue(service instanceof HelpService);
  }

  @Test
  public void parseHelpArgs() {
    String[] args = {"-h"};
    Service service = parser.parseArgs(args);
    Assert.assertTrue(service instanceof HelpService);
  }
}