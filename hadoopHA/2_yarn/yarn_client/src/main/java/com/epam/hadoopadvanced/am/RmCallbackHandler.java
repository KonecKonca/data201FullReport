package com.epam.hadoopadvanced.am;

import com.epam.hadoopadvanced.am.ApplicationMaster.Stage;
import com.epam.hadoopadvanced.utils.ContainerUtils;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.yarn.api.ApplicationConstants;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.api.records.LocalResource;
import org.apache.hadoop.yarn.api.records.NodeReport;
import org.apache.hadoop.yarn.api.records.UpdatedContainer;
import org.apache.hadoop.yarn.client.api.async.AMRMClientAsync.AbstractCallbackHandler;
import org.apache.hadoop.yarn.util.Records;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RmCallbackHandler extends AbstractCallbackHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(RmCallbackHandler.class);
  private ApplicationMaster applicationMaster;

  public RmCallbackHandler(ApplicationMaster applicationMaster) {
    this.applicationMaster = applicationMaster;

  }

  /**
   * Reports the result of completed container. Container treated as successfully complited if its
   * exit status is 0
   *
   * @param list the list of completed containers
   */
  @Override
  public void onContainersCompleted(List<ContainerStatus> list) {
    int nRemainingContainers = applicationMaster.getNCompletedContainers().addAndGet(list.size());
    for (ContainerStatus status : list) {
      LOGGER.info("Container {} has completed, state={}, exitStatus={}, diagnostics={}",
          status.getContainerId(),
          status.getState(),
          status.getExitStatus(),
          status.getDiagnostics());
      if (status.getExitStatus() != 0) {
        applicationMaster.getNFailedContainers().addAndGet(1);
      }
    }
    if (nRemainingContainers == applicationMaster.getNContainers()) {
      applicationMaster.getDone().set(true);
    }
  }

  /**
   * Starts target application map or reduce stage inside the allocated container.
   *
   * @param list the list of containers which was allocated
   */
  @Override
  public void onContainersAllocated(List<Container> list) {
    for (Container container : list) {
      try {
        ContainerLaunchContext ctx =
            Records.newRecord(ContainerLaunchContext.class);

        Map<String, LocalResource> resources = new HashMap<>();
        ContainerUtils.addLocalResource(FileSystem.get(new Configuration()),
            applicationMaster.getJarName(), resources);
        ctx.setLocalResources(resources);

        Map<String, String> env = new HashMap<>();
        ContainerUtils.setupClasspath(env);
        ctx.setEnvironment(env);
        ctx.setCommands(setupCommands());
        LOGGER.info("[AM] Launching container {}", container.getId());
        applicationMaster.getNmClientAsync().startContainerAsync(container, ctx);
      } catch (IOException e) {
        applicationMaster.getNCompletedContainers().addAndGet(1);
        applicationMaster.getNFailedContainers().addAndGet(1);
        LOGGER.error("Error occurs in container when accessing file system: " + e.getMessage(), e);
      }
    }
  }

  /**
   * @return the list of startup arguments for the container's application.
   */
  private List<String> setupCommands() {
    List<String> commands = new LinkedList<>();
    if (applicationMaster.getStage() == Stage.MAPPER) {
      commands.add("java " + applicationMaster.getMapperClass());
      int containerIndex = (applicationMaster.getNAllocatedContainers().addAndGet(1) - 1);
      long startLine =
          applicationMaster.getNFileLines() / applicationMaster.getNContainers() * containerIndex;
      commands.add("--start_line " + startLine);
      long endLine =
          applicationMaster.getNFileLines() / applicationMaster.getNContainers() * (containerIndex
              + 1);
      commands.add("--end_line " + endLine);
      commands.add("--input " + applicationMaster.getInputPath());
    } else {
      commands.add("java " + applicationMaster.getReducerClass());
      commands.add("--output " + applicationMaster.getOutputPath());
    }
    commands.addAll(applicationMaster.getAppArgs());
    commands.add("1>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/app.stdout");
    commands.add("2>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/app.stderr");
    return commands;
  }


  @Override
  public void onContainersUpdated(List<UpdatedContainer> list) {
// nothing to do or report here
  }

  @Override
  public void onShutdownRequest() {
    applicationMaster.getDone().set(true);
  }

  @Override
  public void onNodesUpdated(List<NodeReport> list) {
// nothing to do or report here
  }

  @Override
  public float getProgress() {
    return (float) applicationMaster.getNCompletedContainers().get() / applicationMaster
        .getNContainers();
  }

  @Override
  public void onError(Throwable throwable) {
    LOGGER.error("Error occurs in the container", throwable);
    applicationMaster.getDone().set(true);
  }
}
