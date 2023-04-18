package com.blessedmusicalturkeys.projectreleasenotes.utilities;

import com.blessedmusicalturkeys.projectreleasenotes.constants.ApplicationConstants;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.Transport;
import org.eclipse.jgit.transport.ssh.jsch.JschConfigSessionFactory;
import org.eclipse.jgit.transport.ssh.jsch.OpenSshConfig;
import org.eclipse.jgit.util.FS;

/**
 * Class that connects to the provided GIT repository and performs GIT operations on it
 *
 * @author Timothy Stratton
 */
public class JGit {

  private final String gitPrivateKey;

  private final String CONST_MERGE_PREAMBLE = "Merged in";

  Git git;

  public JGit() throws IOException, GitAPIException {
    this.gitPrivateKey = ApplicationConstants.CONST_GIT_REPOSITORY_PRIVATE_KEY;

    File workingDir = Files.createTempDirectory("workspace").toFile();

    git = Git.cloneRepository()
      .setDirectory(workingDir)
      .setTransportConfigCallback(new SshTransportConfigCallback())
      .setURI(ApplicationConstants.CONST_GIT_REPOSITORY_URL)
      .call();
  }

  public void localGitRepo() throws IOException {
    git = Git.open(new File("."));
  }

  public List<String> listTags() throws MissingObjectException, GitAPIException {
    Set<String> tags = new LinkedHashSet<>();

    Iterable<RevCommit> commits = git.log().call();
    for (RevCommit commit : commits) {
      Map<ObjectId, String> namedCommits = git.nameRev().addPrefix("refs/tags/").add(commit).call();
      if (namedCommits.containsKey(commit.getId()) && !namedCommits.get(commit.getId())
          .contains("~")) {
        tags.add(namedCommits.get(commit.getId()));
      }
    }

    return new ArrayList<>(tags);
  }

  public List<String> getIssuesAddressedSinceLastTag(String fromTag, String toTag)
      throws GitAPIException, MissingObjectException {
    Set<String> issues = new LinkedHashSet<>();

    Iterable<RevCommit> commits = git.log().call();
    for (RevCommit commit : commits) {
      Map<ObjectId, String> namedCommits = git.nameRev().addPrefix("refs/tags/").add(commit).call();
      if (namedCommits.containsKey(commit.getId())
          && commit.getShortMessage().contains(CONST_MERGE_PREAMBLE)
          && (
          namedCommits.get(commit.getId()).contains(fromTag) ||
              namedCommits.get(commit.getId()).contains(toTag)
      )) {
        try {
          String unparsedIssueString = commit.getShortMessage().split(CONST_MERGE_PREAMBLE + " " + ApplicationConstants.CONST_JIRA_PROJECT_KEY)[1];

          String issueNumber = ApplicationConstants.CONST_JIRA_PROJECT_KEY + "-" + unparsedIssueString.split("-")[1];
          issues.add(issueNumber);
        } catch (Exception e) { /* nothing to be done */ }
      }
    }

    return new ArrayList<>(issues);
  }

  private class SshTransportConfigCallback implements TransportConfigCallback {

    private final SshSessionFactory sshSessionFactory = new JschConfigSessionFactory() {
      @Override
      protected void configure(OpenSshConfig.Host hc, Session session) {
        session.setConfig("StrictHostKeyChecking", "no");
      }

      @Override
      protected JSch createDefaultJSch(FS fs) throws JSchException {
        File privateKeyFile = createPrivateKeyFile();
        JSch jSch = super.createDefaultJSch(fs);
        jSch.removeAllIdentity();
        jSch.addIdentity(privateKeyFile.getAbsolutePath());
        return jSch;
      }
    };

    private File createPrivateKeyFile() {
      try {
        File privateKeyFile = Files.createTempFile("id_rsa", "").toFile();
        FileOutputStream outputStream = new FileOutputStream(privateKeyFile);
        byte[] strToBytes = gitPrivateKey.getBytes();
        outputStream.write(strToBytes);
        outputStream.close();
        return privateKeyFile;
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public void configure(Transport transport) {
      SshTransport sshTransport = (SshTransport) transport;
      sshTransport.setSshSessionFactory(sshSessionFactory);
    }

  }
}
