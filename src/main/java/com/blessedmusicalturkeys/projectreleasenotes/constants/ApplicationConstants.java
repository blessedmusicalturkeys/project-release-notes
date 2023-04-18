package com.blessedmusicalturkeys.projectreleasenotes.constants;

import com.blessedmusicalturkeys.projectreleasenotes.utilities.EnvironmentVariables;

public class ApplicationConstants {
  public static final String CONST_GIT_REPOSITORY_URL = EnvironmentVariables.getString("GIT_REPO_URL");
  public static final String CONST_GIT_REPOSITORY_PRIVATE_KEY = EnvironmentVariables.getBase64EncodedString("GIT_PRIVATE_KEY");


  public static final String CONST_JIRA_BASE_URL = EnvironmentVariables.getString("JIRA_URL");
  public static final String CONST_JIRA_USER_NAME = EnvironmentVariables.getString("JIRA_SERVICE_ACCOUNT_USERNAME");
  public static final String CONST_JIRA_API_KEY = EnvironmentVariables.getString("JIRA_SERVICE_ACCOUNT_API_KEY");
  public static final String CONST_JIRA_PROJECT_KEY = EnvironmentVariables.getString("JIRA_PROJECT_KEY");

  public static final Boolean CONST_PREPEND_TO_CHANGELOG = EnvironmentVariables.getBoolean("PREPEND_TO_CHANGELOG");
}
