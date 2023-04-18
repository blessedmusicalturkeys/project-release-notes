package com.blessedmusicalturkeys.projectreleasenotes.utilities;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.blessedmusicalturkeys.projectreleasenotes.constants.ApplicationConstants;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

/**
 * For those projects using JIRA to track issues, this class will connect to your JIRA
 * and link the tickets within source control to the generated changelogs/release notes
 *
 * TODO: A ticket client factory that we pull from based on input args
 *
 * @author Timothy Stratton
 */
public class JiraClient {

  private final JiraRestClient client;

  public JiraClient() throws URISyntaxException {
    JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();

    URI uri = new URI(ApplicationConstants.CONST_JIRA_BASE_URL);
    this.client = factory.createWithBasicHttpAuthentication(uri, ApplicationConstants.CONST_JIRA_USER_NAME,
        ApplicationConstants.CONST_JIRA_API_KEY);
  }

  public Issue getIssue(String issueKey) {
    IssueRestClient issueRestClient = this.client.getIssueClient();

    try {
      return issueRestClient.getIssue(issueKey).claim();
    } catch (RestClientException e) {
      return null;
    }
  }

  public List<Issue> getIssueList(List<String> issueKeys) {
    return issueKeys.stream().map(this::getIssue).filter(Objects::nonNull).toList();
  }

}
