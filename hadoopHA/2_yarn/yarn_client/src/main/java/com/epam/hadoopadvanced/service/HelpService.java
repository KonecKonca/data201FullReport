package com.epam.hadoopadvanced.service;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

public class HelpService implements Service {

  private String syntax;
  private String header;
  private Options options;

  public HelpService(String syntax, String header, Options options) {
    this.options = options;
    this.syntax = syntax;
    this.header = header;
  }

  /**
   * Prints help message to the console.
   */
  @Override
  public void execute() {
    new HelpFormatter().printHelp(syntax, header, options, null, true);
  }
}
