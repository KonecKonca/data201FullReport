package com.epam.hadoopadvanced.am;

import com.epam.hadoopadvanced.service.YarnClientService;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.yarn.api.protocolrecords.RegisterApplicationMasterResponse;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.client.api.AMRMClient.ContainerRequest;
import org.apache.hadoop.yarn.client.api.async.AMRMClientAsync;
import org.apache.hadoop.yarn.client.api.async.NMClientAsync;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationMaster implements Closeable {

  private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationMaster.class);

  @Getter
  private NMClientAsync nmClientAsync;
  @Getter
  private AMRMClientAsync<ContainerRequest> rmClientAsync;
  @Getter
  private String jarName;
  @Getter
  private String mapperClass;
  @Getter
  private String reducerClass;
  @Getter
  private String inputPath;
  @Getter
  private String outputPath;
  @Getter
  private int nContainers;
  @Getter
  private List<String> appArgs;
  @Getter
  private AtomicInteger nCompletedContainers;
  @Getter
  private AtomicInteger nFailedContainers;
  @Getter
  private AtomicInteger nAllocatedContainers;
  @Getter
  private AtomicBoolean done;
  @Getter
  private Stage stage;
  @Getter
  private long nFileLines;

  private String diagnostic;
  private Options options;

  public enum Stage {
    MAPPER, REDUCER
  }

  public static void main(String[] args) {
    try (ApplicationMaster master = new ApplicationMaster()) {
      if (master.init(args)) {
        master.start();
      } else {
        master.printHelp();
        System.exit(-1);
      }
    } catch (IOException | YarnException e) {
      LOGGER.error("Exception occurs in the ApplicationMaster: " + e.getMessage(), e);
      System.exit(1);
    }
  }

  public ApplicationMaster() {
    options = new Options();
    Option jarOtp = new Option("jarName", true, "jar file to launch on the containers");
    jarOtp.setRequired(true);
    options.addOption(jarOtp);
    Option inputOpt = new Option("inputPath", true, "the input file to process");
    inputOpt.setRequired(true);
    options.addOption(inputOpt);
    Option outputOpt = new Option("outputPath", true, "The path where result will be stored");
    outputOpt.setRequired(true);
    options.addOption(outputOpt);
    Option mapperOtp = new Option("mapper", true, "mapper class to launch");
    mapperOtp.setRequired(true);
    options.addOption(mapperOtp);
    Option reducerOtp = new Option("reducer", true, "reducer class to launch");
    reducerOtp.setRequired(true);
    options.addOption(reducerOtp);
    Option parallelismOtp = new Option("parallelism", true, "number of containers to create");
    parallelismOtp.setRequired(true);
    options.addOption(parallelismOtp);

    nmClientAsync = NMClientAsync.createNMClientAsync(new NmCallbackHandler());
    rmClientAsync = AMRMClientAsync.createAMRMClientAsync(100, new RmCallbackHandler(this));
  }

  public boolean init(String[] args) throws IOException {
    CommandLine cli;
    try {
      cli = new GnuParser().parse(options, args);
    } catch (ParseException e) {
      LOGGER.error(e.getMessage());
      return false;
    }
    jarName = cli.getOptionValue("jarName");
    inputPath = cli.getOptionValue("inputPath");
    outputPath = cli.getOptionValue("outputPath");
    mapperClass = cli.getOptionValue("mapper");
    reducerClass = cli.getOptionValue("reducer");
    nContainers = Integer.parseInt(cli.getOptionValue("parallelism"));
    appArgs = cli.getArgList();
    nmClientAsync.init(new YarnConfiguration());
    nmClientAsync.start();
    rmClientAsync.init(new YarnConfiguration());
    rmClientAsync.start();
    nCompletedContainers = new AtomicInteger();
    nFailedContainers = new AtomicInteger();
    nAllocatedContainers = new AtomicInteger();
    done = new AtomicBoolean(false);
    computeMapperOffsets();
    return true;
  }

  private void computeMapperOffsets() throws IOException {
    FileSystem fs = FileSystem.get(new Configuration());
    Path path = new Path(inputPath);
    if (!fs.exists(path)) {
      LOGGER.error("File not found: {}", inputPath);
      System.exit(-2);
    }
    try (FSDataInputStream in = fs.open(path);
        InputStreamReader inReader = new InputStreamReader(in);
        BufferedReader bufferedReader = new BufferedReader(inReader)) {
      nFileLines = bufferedReader.lines().count();
    }
  }

  private void start() throws IOException, YarnException {
    RegisterApplicationMasterResponse amResponse = rmClientAsync
        .registerApplicationMaster("", 0, "");
    LOGGER.info("Application master is registered.");
    LOGGER.info("MAPPER STAGE");
    mapperStage(amResponse);
    FinalApplicationStatus status = getLastStageStatus();
    if (status == FinalApplicationStatus.SUCCEEDED) {
      LOGGER.info("Mapper stage finished successfully");
      LOGGER.info("REDUCER STAGE");
      reducerStage(amResponse);
      status = getLastStageStatus();
    }
    rmClientAsync.unregisterApplicationMaster(status, diagnostic, "");
    LOGGER.info("Application master is unregistered.");
  }

  private void mapperStage(RegisterApplicationMasterResponse amResponse) {
    stage = Stage.MAPPER;
    long mapperMemory = (amResponse.getMaximumResourceCapability().getMemorySize()
        - YarnClientService.MEMORY_REQUEST) / nContainers;
    registerContainers(nContainers, mapperMemory);
    LOGGER.info("Application master is waiting for mapper stage to finish");
    waitForFinish();
  }

  private void reducerStage(RegisterApplicationMasterResponse amResponse) {
    stage = Stage.REDUCER;
    nContainers = 1;
    nCompletedContainers.set(0);
    nAllocatedContainers.set(0);
    done.set(false);
    long reducerMemory = amResponse.getMaximumResourceCapability().getMemorySize()
        - YarnClientService.MEMORY_REQUEST;
    registerContainers(nContainers, reducerMemory);
    LOGGER.info("Application master is waiting for reducer stage to finish");
    waitForFinish();
  }

  private void waitForFinish() {
    while (!done.get()) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        LOGGER.warn("Application master monitoring thread is interrupted.");
        Thread.currentThread().interrupt();
        System.exit(-1);
      }
    }
  }

  /**
   * Returns the status of the last application {@link Stage}. If it is not and SUCCEEDED status,
   * set {@link ApplicationMaster#diagnostic} message, describing the possible error.
   *
   * @return status of the last application stage
   */
  private FinalApplicationStatus getLastStageStatus() {
    FinalApplicationStatus status = FinalApplicationStatus.SUCCEEDED;
    if (nFailedContainers.get() != 0 && nCompletedContainers.get() == nContainers) {
      status = FinalApplicationStatus.FAILED;
      diagnostic = String.format("Diagnostics: total=%d, completed=%d, failed=%d", nContainers,
          nCompletedContainers.get(), nFailedContainers.get());
    }
    return status;
  }

  /**
   * Sends the request to ResourceManager to allocate containers.
   *
   * @param nContainers the number if containers to allocate
   * @param containerMemory the memory for one container
   */
  private void registerContainers(int nContainers, long containerMemory) {
    Priority priority = Priority.newInstance(0);
    Resource capability = Resource.newInstance(containerMemory, 1);
    for (int i = 0; i < nContainers; ++i) {
      rmClientAsync.addContainerRequest(new ContainerRequest(capability, null, null, priority));
    }
  }

  @Override
  public void close() throws IOException {
    nmClientAsync.close();
    rmClientAsync.close();
  }

  private void printHelp() {
    new HelpFormatter().printHelp("java " + this.getClass().getTypeName(),
        "ApplicationMaster class", options, null, true);
  }
}