package com.blessedmusicalturkeys.projectreleasenotes;

import com.blessedmusicalturkeys.projectreleasenotes.cli.CLIStrategy;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import org.eclipse.jgit.api.errors.GitAPIException;

/**
 * Entry point for the application which uses strategy pattern to determine how the request should be handled.
 *
 * @author Timothy Stratton
 */
public class ProjectReleaseNotesApplication {

  public static void main(String[] args) {
    List<CLIStrategy> strategies = CLIStrategy.getAllStrategies();

    for (CLIStrategy strategy : strategies) {
      if (strategy.canHandle(args)) {
        strategy.handleRequest(args);
        break;
      }
    }
  }

}
