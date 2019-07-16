package com.epam.hadoopadvanced.am;

import java.nio.ByteBuffer;
import java.util.Map;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.client.api.async.NMClientAsync.AbstractCallbackHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NmCallbackHandler extends AbstractCallbackHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(NmCallbackHandler.class);

  @Override
  public void onContainerStarted(ContainerId containerId, Map<String, ByteBuffer> map) {
    LOGGER.info("Container {} was started", containerId);
  }

  @Override
  public void onContainerStatusReceived(ContainerId containerId,
      ContainerStatus containerStatus) {
    LOGGER.info("Container {}, status received {}", containerId, containerStatus);
  }

  @Override
  public void onContainerStopped(ContainerId containerId) {
    LOGGER.info("Container {} was stopped", containerId);
  }

  @Override
  public void onStartContainerError(ContainerId containerId, Throwable throwable) {
    LOGGER.error(
        String.format("Container %s, failed to start: %s", containerId,
            throwable.getMessage()),
        throwable);
  }

  @Override
  public void onContainerResourceIncreased(ContainerId containerId, Resource resource) {
    LOGGER.info("Container {}, resources was increased {}", containerId, resource);
  }

  @Override
  public void onContainerResourceUpdated(ContainerId containerId, Resource resource) {
    LOGGER.info("Container {}, resources was updated {}", containerId, resource);
  }

  @Override
  public void onGetContainerStatusError(ContainerId containerId, Throwable throwable) {
    LOGGER.error(
        String.format("Container %s, status error: %s", containerId,
            throwable.getMessage()),
        throwable);
  }

  @Override
  public void onIncreaseContainerResourceError(ContainerId containerId, Throwable throwable) {
    LOGGER.error(
        String.format("Container %s, error when increasing resources: %s", containerId,
            throwable.getMessage()),
        throwable);
  }

  @Override
  public void onUpdateContainerResourceError(ContainerId containerId, Throwable throwable) {
    LOGGER.error(
        String.format("Container %s, Resource error %s", containerId, throwable.getMessage()),
        throwable);
  }

  @Override
  public void onStopContainerError(ContainerId containerId, Throwable throwable) {
    LOGGER.error(
        String.format("Container %s, failed to stop %s", containerId, throwable.getMessage()),
        throwable);
  }
}