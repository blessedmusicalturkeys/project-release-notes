# Project Release Notes Home @github


## What is Project Release Notes

project-release-notes is a command-line base application that will allow users to generate release notes, changelogs, and more for their project via simple java commands

# Usage
`.drone.yml`
```yaml
steps:
  - name: generate-changelog
    image: maven:3.8.5-openjdk-17-slim
    commands:
      - mvn clean dependency:copy-dependencies
      - java -cp target/dependency/project-release-notes-0.0.4.jar com.blessedmusicalturkeys.projectreleasenotes.ProjectReleaseNotesApplication -c --incrementVersion=PATCH
    environment:
      GIT_PRIVATE_KEY: ${GIT_PRIVATE_KEY}
      GIT_REPO_URL: ${GIT_REPO_URL}
      GIT_WORKING_TRUNK: ${GIT_WORK_TRUNK}
      JIRA_PROJECT_KEY: ${JIRA_PROJECT_KEY}
      JIRA_SERVICE_ACCOUNT_API_KEY: ${JIRA_SERVICE_ACCOUNT_API_KEY}
      JIRA_SERVICE_ACCOUNT_USERNAME: ${JIRA_SERVICE_ACCOUNT_USERNAME}
      JIRA_URL: ${JIRA_URL}
      PREPEND_TO_CHANGELOG: ${PREPEND_TO_CHANGELOG}
```

`Command line`
```shell
$ cd <project root directory wtih project-release-notes as a pom dependency>
$ <export the necessary environment variables (see above drone variables), or have them setup in your environment already>
$ mvn clean dependency:copy-dependencies
$ java -cp target/dependency/project-release-notes-0.0.4.jar com.blessedmusicalturkeys.projectreleasenotes.ProjectReleaseNotesApplication -c --incrementVersion=PATCH
```

# Dependency

## Maven

```xml
<dependency>
  <groupId>io.github.blessedmusicalturkeys</groupId>
  <artifactId>project-release-notes</artifactId>
  <version>0.0.4</version>
</dependency>
```

## Gradle
```groovy
compile group: 'io.github.blessedmusicalturkeys', name: 'project-release-notes', version: '0.0.4'
```

# Shout Outs!

[NateDog12501](https://github.com/NateDogg12501) for his insights and direction on this project! It wouldn't be here without his help! Go check out [his page](https://github.com/NateDogg12501?tab=repositories) to see some of the awesome stuff he's doing!


# Future Works
- [ ] Finish implementing the various CLI strategies
- [ ] Refine and mature TAG workflow to industry standards (GitFlow?)
- [ ] ChatGPT! (because buzzwords)
- [ ] OMG better logging!