package com.epam.hadoopadvanced.service;

import com.epam.hadoopadvanced.am.ApplicationMaster;
import com.epam.hadoopadvanced.utils.ContainerUtils;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import lombok.Data;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.yarn.api.ApplicationConstants;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationSubmissionContext;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.api.records.LocalResource;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
public class YarnClientService implements Service {

  public static final int MEMORY_REQUEST = 128;
  private static final Logger LOGGER = LoggerFactory.getLogger(YarnClientService.class);
  private String appJar;
  private String mapperClass;
  private String reducerClass;
  private String inputPath;
  private String outputPath;
  private String appName;
  private String queue;
  private int parallelism;
  private List<String> jarArgs;

  /**
   * Launches ApplicationMaster container. ApplicationMaster implementation is {@link
   * ApplicationMaster}
   */
  public void execute() {
    try (YarnClient yarnClient = YarnClient.createYarnClient()) {
      yarnClient.init(new YarnConfiguration());
      yarnClient.start();
      ApplicationSubmissionContext appContext = createAmSubmitContext(yarnClient);
      ApplicationId appId = appContext.getApplicationId();
      yarnClient.submitApplication(appContext);
      monitor(yarnClient, appId);
    } catch (IOException | YarnException e) {
      LOGGER.error("Cannot create application cause of exception: " + e.getMessage(), e);
    }
  }

  /**
   * Specifies resources, name, queue and application to run submit on YARN.
   *
   * @param yarnClient YARN instance
   * @return the application context
   * @throws IOException if application resources missed
   * @throws YarnException if YARN cannot create application
   */
  private ApplicationSubmissionContext createAmSubmitContext(YarnClient yarnClient)
      throws IOException, YarnException {
    ApplicationSubmissionContext appContext =
        yarnClient.createApplication().getApplicationSubmissionContext();
    ContainerLaunchContext containerContext = createContainerContext();
    appContext.setAMContainerSpec(containerContext);
    appContext.setApplicationName(appName);
    appContext.setQueue(queue);

    Resource capability = Resource.newInstance(MEMORY_REQUEST, 1);
    appContext.setResource(capability);
    return appContext;
  }

  /**
   * Specifies container context for the AM
   *
   * @return the container context for the AM
   * @throws IOException if container's resources missed
   */
  private ContainerLaunchContext createContainerContext() throws IOException {
    FileSystem fs = FileSystem.get(new Configuration());
    Map<String, LocalResource> localResources = new HashMap<>();
    String jarName = System.getProperty("jar.name") + ".jar";
    fs.copyFromLocalFile(true, new Path(jarName), new Path(jarName));
    ContainerUtils.addLocalResource(fs, jarName, localResources);

    Map<String, String> env = new HashMap<>();
    ContainerUtils.setupClasspath(env);
    return ContainerLaunchContext
        .newInstance(localResources, env, createCommands(), null, null, null);
  }

  /**
   * Specifies commands to launch AM on the container.
   *
   * @return the list of commands to launch
   */
  private List<String> createCommands() {
    List<String> commands = new LinkedList<>();
    commands.add("java " + ApplicationMaster.class.getTypeName());
    commands.add("-jarName " + appJar);
    commands.add("-inputPath " + inputPath);
    commands.add("-outputPath " + outputPath);
    commands.add("-mapper " + mapperClass);
    commands.add("-reducer " + reducerClass);
    commands.add("-parallelism " + parallelism);
    commands.addAll(jarArgs);
    commands.add("1>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/am.stdout");
    commands.add("2>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/am.stderr");
    return commands;
  }

  private void monitor(YarnClient yarnClient, ApplicationId appId)
      throws IOException, YarnException {
    YarnApplicationState appState = getAmState(yarnClient, appId);
    try {
      while (appState != YarnApplicationState.FINISHED &&
          appState != YarnApplicationState.KILLED &&
          appState != YarnApplicationState.FAILED) {
        Thread.sleep(100);
        appState = getAmState(yarnClient, appId);
      }
      LOGGER.info(
          "Application {} finished with state {} at {}", appId, appState,
          yarnClient.getApplicationReport(appId).getFinishTime());
    } catch (InterruptedException e) {
      LOGGER.warn("Client monitoring loop was interrupted.", e);
      Thread.currentThread().interrupt();
    }
  }

  private YarnApplicationState getAmState(YarnClient yarnClient, ApplicationId appId)
      throws IOException, YarnException {
    return yarnClient.getApplicationReport(appId).getYarnApplicationState();
  }
}