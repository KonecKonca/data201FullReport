package com.epam.hadoopadvanced.utils;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.yarn.api.ApplicationConstants.Environment;
import org.apache.hadoop.yarn.api.records.LocalResource;
import org.apache.hadoop.yarn.api.records.LocalResourceType;
import org.apache.hadoop.yarn.api.records.LocalResourceVisibility;
import org.apache.hadoop.yarn.api.records.URL;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.util.Apps;

public final class ContainerUtils {

  private ContainerUtils() {
  }

  /**
   * Adds file from file system to the container resources.
   *
   * @param fs the target file system
   * @param filePath the path of the file in the local file system
   * @param localResources map to store resources
   * @throws IOException if the file not exists
   */
  public static void addLocalResource(FileSystem fs, String filePath,
      Map<String, LocalResource> localResources) throws IOException {
    Path dst = new Path(filePath);
    FileStatus scFileStatus = fs.getFileStatus(dst);
    LocalResource scRsrc = LocalResource.newInstance(
        URL.fromPath(dst),
        LocalResourceType.FILE, LocalResourceVisibility.APPLICATION,
        scFileStatus.getLen(), scFileStatus.getModificationTime());
    localResources.put(filePath, scRsrc);
  }

  /**
   * Setup classpath for the container.
   */
  public static void setupClasspath(Map<String, String> env) {
    for (String c : new YarnConfiguration().getStrings(YarnConfiguration.YARN_APPLICATION_CLASSPATH,
        YarnConfiguration.DEFAULT_YARN_APPLICATION_CLASSPATH)) {
      Apps.addToEnvironment(env, Environment.CLASSPATH.name(), c.trim(), ":");
    }
    Apps.addToEnvironment(env, Environment.CLASSPATH.name(),
        Environment.PWD.$() + File.separator + "*", ":");
  }
}
