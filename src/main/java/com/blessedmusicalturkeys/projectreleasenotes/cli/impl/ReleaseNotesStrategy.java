package com.blessedmusicalturkeys.projectreleasenotes.cli.impl;

import com.blessedmusicalturkeys.projectreleasenotes.cli.CLIStrategy;

/**
 * Generates the Release Notes for a tag of the configured project
 *
 * @author Timothy Stratton
 */
public class ReleaseNotesStrategy implements CLIStrategy {

  @Override
  public boolean canHandle(String... inputArguments) {

    return CLIStrategy.globalCanHandleRequestChecker("-r", "--release-notes", inputArguments);
  }

  @Override
  public void handleRequest(String... inputArgument) {
    System.out.println("Not yet implemented");
  }
}
