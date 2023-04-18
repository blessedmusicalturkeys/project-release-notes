package com.blessedmusicalturkeys.projectreleasenotes.cli.impl;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.blessedmusicalturkeys.projectreleasenotes.cli.CLIStrategy;
import com.blessedmusicalturkeys.projectreleasenotes.utilities.ChangelogGenerator;
import com.blessedmusicalturkeys.projectreleasenotes.utilities.JGit;
import com.blessedmusicalturkeys.projectreleasenotes.utilities.JiraClient;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.MissingObjectException;

/**
 * Generates the Changelog for the configured project
 *
 * @author Timothy Stratton
 */
public class ChangelogStrategy implements CLIStrategy {

  @Override
  public boolean canHandle(String... inputArguments) {
    return CLIStrategy.globalCanHandleRequestChecker("-c", "--changelog", inputArguments);
  }

  @Override
  public void handleRequest(String... inputArgument) {
    int numOfInputArguments = inputArgument.length;

    Integer fromTagIndex = Integer.valueOf(1);
    Integer toTagIndex = Integer.valueOf(0);

    JGit gitClient;
    JiraClient jiraClient;
    ChangelogGenerator changelogGenerator;

    try {
      gitClient = new JGit();
      jiraClient = new JiraClient();
      changelogGenerator = new ChangelogGenerator();
    } catch (GitAPIException | IOException | URISyntaxException e) {
      System.out.println("Initialization exception: [" + e.getMessage() + "]");
      throw new RuntimeException(e);
    }

    List<String> tags;
    try {
      tags = gitClient.listTags();
    } catch (MissingObjectException | GitAPIException e) {
      System.out.println("Unable to retrieve tags for the GIT repo due to: [" + e.getMessage() + "]");
      throw new RuntimeException(e);
    }
    Collections.reverse(tags);

    if (numOfInputArguments == 2) {
      toTagIndex = tags.indexOf(inputArgument[1]);
      fromTagIndex = toTagIndex + 1;
    } else if (numOfInputArguments == 3) {
      fromTagIndex = tags.indexOf(inputArgument[1]);
      toTagIndex = tags.indexOf(inputArgument[2]);
    }

    if (fromTagIndex == -1 || toTagIndex == -1) {
      System.out.println("The provided tag(s) are invalid. Please make sure that you're providing tags that exist in Source Control");
      return;
    }

    try {
      List<String> issueKeys = gitClient.getIssuesAddressedSinceLastTag(tags.get(fromTagIndex),
          tags.get(toTagIndex));

      List<Issue> jiraIssues = jiraClient.getIssueList(issueKeys);

      changelogGenerator.generateChangelogFromExisting(tags.get(toTagIndex), jiraIssues);
    } catch (GitAPIException | IOException e) {
      System.out.println("Unable to generate the changelog due to: [" + e.getMessage() + "]");
      throw new RuntimeException(e);
    }
  }
}
