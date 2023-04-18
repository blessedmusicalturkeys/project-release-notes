package com.blessedmusicalturkeys.projectreleasenotes.cli;

import com.blessedmusicalturkeys.projectreleasenotes.cli.impl.HelpStrategy;
import com.blessedmusicalturkeys.projectreleasenotes.cli.impl.ChangelogStrategy;
import com.blessedmusicalturkeys.projectreleasenotes.cli.impl.ReleaseNotesStrategy;
import com.blessedmusicalturkeys.projectreleasenotes.cli.impl.TagStrategy;
import java.util.ArrayList;
import java.util.List;

/**
 * Decided to use the Strategy pattern to handle incoming command-line requests
 *
 * @author    Timothy Stratton
 */

public interface CLIStrategy {
  boolean canHandle(String... inputArguments);
  void handleRequest(String... inputArgument);

  static boolean globalCanHandleRequestChecker(String abbreviatedHandle, String verboseHandle, String[] inputArguments) {
    return inputArguments != null
        && (
        inputArguments[0].equals(abbreviatedHandle)
            || inputArguments[0].equals(verboseHandle)
    );
  }

  /**
   * This method is used to control the global strategy resolver.
   * We control both what strategies are visible here
   * and the order in which they strategies might be checked and applied.
   *
   * @return {@link CLIStrategy List of CLIStrategy} containing all possible strategies
   */
  static List<CLIStrategy> getAllStrategies() {
    List<CLIStrategy> strategies = new ArrayList<>();

    //Order matters here
    strategies.add(new HelpStrategy());
    strategies.add(new ChangelogStrategy());
    strategies.add(new ReleaseNotesStrategy());
    strategies.add(new TagStrategy());

    return strategies;
  }
}
