# GitHub Activity CLI

A simple command-line application that fetches and displays a GitHub
user's recent public activity events using the GitHub REST API.

https://roadmap.sh/projects/github-user-activity

This tool allows you to quickly check events such as pushes, pull
requests, stars, forks, releases, and more --- directly from your
terminal.

------------------------------------------------------------------------

## Features

- Fetches public GitHub activity for any username\
- Supports multiple event types (Push, Pull Request, Star, Fork,
  Create, Delete, etc.)\
- Clean and human-readable output\
- Easy to run via `.bat` file on Windows\
- Packaged as a standalone JAR with all dependencies included

------------------------------------------------------------------------

## Requirements

- **Java 21+**\
- Internet connection\
- A GitHub username to look up

------------------------------------------------------------------------

## Project Structure

    /src
      /main
        /java/io/github/rodrigocorreiainf/Main.java

    github-activity.bat
    pom.xml
    target/github-activity-1.0-SNAPSHOT-jar-with-dependencies.jar

------------------------------------------------------------------------

## Build Instructions

To build the project:

``` bash
mvn clean package
```

The runnable JAR will be generated at:

    target/github-activity-1.0-SNAPSHOT-jar-with-dependencies.jar

------------------------------------------------------------------------

## Running the Program

### **Option 1 --- Using the .bat file (Windows recommended)**

Run:

    github-activity.bat <github-username>

Example:

    github-activity.bat octocat

The `.bat` file contains:

    @echo off
    java -jar "target\github-activity-1.0-SNAPSHOT-jar-with-dependencies.jar" %*

------------------------------------------------------------------------

### **Option 2 --- Running manually using Java**

    java -jar target/github-activity-1.0-SNAPSHOT-jar-with-dependencies.jar <github-username>

Example:

    java -jar target/github-activity-1.0-SNAPSHOT-jar-with-dependencies.jar torvalds

------------------------------------------------------------------------

## Example Output

    - Pushed 3 commits to rodrigo/my-repo
    - Created a new branch 'feature/login' in rodrigo/web-app
    - Starred microsoft/vscode
    - Forked spring-projects/spring-framework
    - Published release in rodrigo/my-tool

------------------------------------------------------------------------

## Notes

- GitHub API rate-limits unauthenticated requests. If exceeded, you
  may temporarily receive limited or empty responses.\
- Only **public events** are visible --- private GitHub activity
  cannot be accessed.

------------------------------------------------------------------------

## License

This project is licensed under the **MIT License**.\
Feel free to modify, extend, or distribute it.
