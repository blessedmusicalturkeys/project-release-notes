package com.blessedmusicalturkeys.projectreleasenotes.utilities;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.blessedmusicalturkeys.projectreleasenotes.constants.ApplicationConstants;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import net.steppschuh.markdowngenerator.rule.HorizontalRule;
import net.steppschuh.markdowngenerator.text.heading.Heading;

/**
 * Generates the Changelog for a given List of JIRA issues
 *
 * @author Timothy Stratton
 */
public class ChangelogGenerator {

  public void generateChangelogFromExisting(File repoDir, String releaseName, List<Issue> issues) throws IOException {
    StringBuilder changelogBuilder = new StringBuilder();

    changelogBuilder.append(new Heading("Release " + releaseName, 1)).append("\n");

    //Story tickets
    changelogBuilder.append(new Heading("Stories Completed", 2)).append("\n");
    for (Issue issue : issues) {
      if (issue.getIssueType().getName().equals("Story")) {
        changelogBuilder.append(new Heading(issue.getKey(), 3)).append("\n");
        changelogBuilder.append(new Heading(issue.getSummary(), 4)).append("\n");
        changelogBuilder.append(new Heading(issue.getDescription(), 5)).append("\n\n");
      }
    }

    //Bug Tickets
    changelogBuilder.append(new Heading("Bugs Fixed", 2)).append("\n");
    for (Issue issue : issues) {
      if (issue.getIssueType().getName().equals("Bug")) {
        changelogBuilder.append(new Heading(issue.getKey(), 3)).append("\n");
        changelogBuilder.append(new Heading(issue.getSummary(), 4)).append("\n");
        changelogBuilder.append(new Heading(issue.getDescription(), 5)).append("\n\n");
      }
    }

    writeChangeLog(repoDir, changelogBuilder.toString(), ApplicationConstants.CONST_PREPEND_TO_CHANGELOG);
  }

  public void writeChangeLog(File repoDir, String fileContent, boolean... prepend) throws IOException {
    File changelogDirectory = new File(repoDir.getAbsolutePath() + "/changelog");
    changelogDirectory.mkdirs();
    File changelogFile = new File(changelogDirectory, "changelog.md");
    if (!changelogFile.exists()) {
      changelogFile.createNewFile();
    }

    if (prepend != null && prepend[0]) {
      FileInputStream fis = new FileInputStream(changelogFile);
      byte[] buffer = new byte[10];
      StringBuilder sb = new StringBuilder();
      while (fis.read(buffer) != -1) {
        sb.append(new String(buffer));
        buffer = new byte[10];
      }
      fis.close();

      String content = sb.toString();

      fileContent = fileContent + new HorizontalRule(20, HorizontalRule.ASTERISK) + "\n\n" + content;
    }

    FileOutputStream outputStream = new FileOutputStream(changelogFile);
    byte[] strToBytes = fileContent.getBytes();
    outputStream.write(strToBytes);
    outputStream.close();
  }
}
