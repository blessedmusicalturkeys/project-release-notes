package com.blessedmusicalturkeys.projectreleasenotes.cli.impl;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.blessedmusicalturkeys.projectreleasenotes.cli.CLIStrategy;
import com.blessedmusicalturkeys.projectreleasenotes.constants.SemanticVersion;
import com.blessedmusicalturkeys.projectreleasenotes.utilities.ChangelogGenerator;
import com.blessedmusicalturkeys.projectreleasenotes.utilities.JGit;
import com.blessedmusicalturkeys.projectreleasenotes.utilities.JiraClient;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.MissingObjectException;

/**
 * Generates the Changelog for the configured project
 *
 * @author Timothy Stratton
 */
@Slf4j
public class ChangelogStrategy implements CLIStrategy {

  @Override
  public boolean canHandle(String... inputArguments) {
    return CLIStrategy.globalCanHandleRequestChecker("-c", "--changelog", inputArguments);
  }

  @Override
  public void handleRequest(String... inputArgument) {
    log.info("Changelog Generation request received...");

    JGit gitClient;
    JiraClient jiraClient;
    ChangelogGenerator changelogGenerator;

    log.info("Initializing the System...");
    try {
      gitClient = new JGit();
      jiraClient = new JiraClient();
      changelogGenerator = new ChangelogGenerator();
    } catch (GitAPIException | IOException | URISyntaxException e) {
      log.error("Initialization exception: [{}]", e.getMessage(), e);
      throw new RuntimeException(e);
    }

    log.info("Pulling all existing tags...");
    List<String> tags  = getAllTags(gitClient);

    log.info("Generating changelog...");
    String tagName = processGenerateChangelogRequest(gitClient, jiraClient, changelogGenerator, tags, inputArgument);

    log.info("Committing changelog to new tag, merging to the make working trunk, and pushing...");
    try {
      gitClient.commitChangelogTagAndPush(tagName);
    } catch (GitAPIException | IOException e) {
      log.error("Unable to commit the changelog due to: [{}]", e.getMessage(), e);
      throw new RuntimeException(e);
    }

    log.info("Changelog Generation Complete.");
  }

  private String processGenerateChangelogRequest(JGit gitClient, JiraClient jiraClient, ChangelogGenerator changelogGenerator,
      List<String> tags, String... inputArgument) {
    String tagName;
    int numOfInputArguments = inputArgument.length;

    try {
      if (numOfInputArguments == 2 && inputArgument[1].startsWith("--incrementVersion")) {
        String versioningStrategy = inputArgument[1].split("--incrementVersion=")[1];
        List<String> issueKeys = gitClient.getAllIssuesSinceLastTag();

        tagName = incrementReleaseNumber(tags, versioningStrategy);

        generateChangelog(gitClient, jiraClient, changelogGenerator, tagName, issueKeys);
      } else if (numOfInputArguments == 2 && inputArgument[1].startsWith("--tag")) {
        String tagToGenerateChangelogFor = inputArgument[1].split("--tag=")[1];
        int tagIndex = tags.indexOf(tagToGenerateChangelogFor);
        tagName = tags.get(tagIndex);

        List<String> issueKeys = gitClient.getIssuesWithinTag(tagName);

        generateChangelog(gitClient, jiraClient, changelogGenerator, tagName, issueKeys);
      } else if (numOfInputArguments == 2 && inputArgument[1].startsWith("--full")) {
        for (String tag : tags) {
          List<String> issueKeys = gitClient.getIssuesWithinTag(tag);

          generateChangelog(gitClient, jiraClient, changelogGenerator, tag, issueKeys);
        }
        tagName = "full-changelog-generation";
      } else {
        log.info("Unsupported Operation requested. Rerun with `--help` option to see available operations");
        throw new RuntimeException("Unsupported Operation");
      }
    } catch (GitAPIException | IOException e) {
      log.error("Unable to generate the changelog due to: [{}]", e.getMessage() , e);
      throw new RuntimeException(e);
    }

    return tagName;
  }

  private String incrementReleaseNumber(List<String> tags, String versioningStrategy) {
    String tagName = null;
    SemanticVersion incrementVersionBy;
    try {
      incrementVersionBy = SemanticVersion.valueOf(versioningStrategy);
    } catch (IllegalArgumentException e) {
      String semanticVersioningRegex = "^([0-9]+)\\.([0-9]+)\\.([0-9]+)(?:-([0-9A-Za-z-]+(?:\\.[0-9A-Za-z-]+)*))?(?:\\+[0-9A-Za-z-]+)?$";

      if (versioningStrategy.matches(semanticVersioningRegex)) {
        return versioningStrategy;
      } else {
        log.info("Version flag must equal: `MAJOR`, `MINOR`, `PATCH`, or provide an explicit semantic version, e.g. 1.2.3");
        throw new RuntimeException("Unsupported Versioning Strategy");
      }
    }

    String[] lastTag = tags.get(tags.size()-1).split("\\.");

    if (SemanticVersion.MAJOR == incrementVersionBy) {
      int majorVersion = Integer.parseInt(lastTag[0])+1;
      tagName = majorVersion + ".0.0";
    } else if (SemanticVersion.MINOR == incrementVersionBy) {
      int minorVersion = Integer.parseInt(lastTag[1])+1;
      tagName = lastTag[0] + "." + minorVersion + ".0";
    } else if (SemanticVersion.PATCH == incrementVersionBy) {
      int patchVersion = Integer.parseInt(lastTag[2])+1;
      tagName = lastTag[0] + "." + lastTag[1] + "." + patchVersion;
    }

    return tagName;
  }

  private List<String> getAllTags(JGit gitClient) {
    List<String> tags;
    try {
      tags = gitClient.listTags();
    } catch (MissingObjectException | GitAPIException e) {
      log.error("Unable to retrieve tags for the GIT repo due to: [{}]", e.getMessage(), e);
      throw new RuntimeException(e);
    }
    Collections.reverse(tags);
    return tags;
  }

  private void generateChangelog(JGit gitClient, JiraClient jiraClient, ChangelogGenerator changelogGenerator, String tagName, List<String> jiraIssueKeys)
      throws IOException {
    List<Issue> jiraIssues = jiraClient.getIssueList(jiraIssueKeys);

    changelogGenerator.generateChangelogFromExisting(gitClient.getWorkingDir(), tagName, jiraIssues);
  }
}
