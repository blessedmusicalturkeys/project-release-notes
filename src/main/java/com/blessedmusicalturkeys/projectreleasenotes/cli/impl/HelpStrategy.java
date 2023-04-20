package com.blessedmusicalturkeys.projectreleasenotes.cli.impl;

import com.blessedmusicalturkeys.projectreleasenotes.cli.CLIStrategy;

/**
 * Simple Helper processors which provides users with all possible configurations
 * and operations available to them
 *
 * @author    Timothy Stratton
 */

public class HelpStrategy implements CLIStrategy {

  @Override
  public boolean canHandle(String... inputArguments) {
    return CLIStrategy.globalCanHandleRequestChecker("-h", "--help", inputArguments);
  }

  @Override
  public void handleRequest(String... inputArgument) {
    StringBuilder output = new StringBuilder();

    output.append("Usage: ").append("\n");
    output.append("\tjava -cp project-release-notes.jar com.blessedmusicalturkeys.projectreleasenotes.ProjectReleaseNotesApplication (-h | --help)").append("\n");
    output.append("\tjava -cp project-release-notes.jar com.blessedmusicalturkeys.projectreleasenotes.ProjectReleaseNotesApplication (-c | --changelog) --incrementVersion=(MAJOR | MINOR | PATCH)").append("\n");
    output.append("\tjava -cp project-release-notes.jar com.blessedmusicalturkeys.projectreleasenotes.ProjectReleaseNotesApplication (-c | --changelog) --tag=<tag-name").append("\n");
    output.append("\tjava -cp project-release-notes.jar com.blessedmusicalturkeys.projectreleasenotes.ProjectReleaseNotesApplication (-c | --changelog) --full").append("\n");
    output.append("\tjava -cp project-release-notes.jar com.blessedmusicalturkeys.projectreleasenotes.ProjectReleaseNotesApplication (-t | --tag) <tag-name>").append("\n");
    output.append("\tjava -cp project-release-notes.jar com.blessedmusicalturkeys.projectreleasenotes.ProjectReleaseNotesApplication (-r | --release-notes) <email-to-send-release-notes-to> [<space-delimited-emails-to-send-release-notes-to>...]").append("\n");

    System.out.println(output);
  }
}
