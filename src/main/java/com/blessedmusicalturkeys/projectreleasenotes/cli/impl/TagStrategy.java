package com.blessedmusicalturkeys.projectreleasenotes.cli.impl;

import com.blessedmusicalturkeys.projectreleasenotes.cli.CLIStrategy;

/**
 * Tags the configured project and pushes the tag to origin
 *
 * @author Timothy Stratton
 */
public class TagStrategy implements CLIStrategy {

  @Override
  public boolean canHandle(String... inputArguments) {

    return CLIStrategy.globalCanHandleRequestChecker("-t", "--tag", inputArguments);
  }

  @Override
  public void handleRequest(String... inputArgument) {
    System.out.println("Not yet implemented");
  }
}
