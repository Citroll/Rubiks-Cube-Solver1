Upgrade guide: move project to Java 21 (LTS)

Overview
- The automatic Copilot Java upgrade tool is unavailable on this account. This guide explains manual steps to upgrade the project to Java 21 and verify the build.

Prerequisites
- Download and install a Java 21 JDK (Adoptium/Eclipse Temurin or Azul Zulu). Example: Temurin 21.
- Make note of the Java 21 installation path (e.g., `C:\Program Files\Eclipse Adoptium\jdk-21.0.2.7-hotspot`).
- Git available for commits.

Steps
1. Install Java 21
   - Download a JDK 21 distribution and install it.
   - Verify installation in PowerShell:

     java --version
     javac --version

2. Update JAVA_HOME and PATH (temporary for session)
   - Open PowerShell and run:

     $env:JAVA_HOME = 'C:\path\to\jdk-21'
     $env:Path = "$env:JAVA_HOME\\bin;" + $env:Path

3. Build and run the project (no build tool present)
   - This repository appears to be a simple plain-Java project without Maven/Gradle wrapper files.
   - Compile sources and run from repository root (PowerShell):

     mkdir -Force build\classes
     javac -d build/classes -source 21 -target 21 src\rubikscube\*.java
     java -cp build/classes rubikscube.Solver testcases\base.txt output.txt

   - If compilation fails, inspect errors and apply fixes. Common issues when moving to Java 21:
     - Removed/encapsulated internal JDK APIs (sun.*). Avoid depends on them.
     - Source/target compatibility: use `--release 21` with modern JDKs instead of `-source`/`-target`.

4. Optional: add a simple PowerShell script to build with the specified JDK (see `scripts\use-java21.ps1`).

5. Commit changes once build succeeds:

   git checkout -b upgrade/java-21
   git add .
   git commit -m "Upgrade project to Java 21: set compilation target, update docs"

Notes on automated code transformations
- The Copilot/OpenRewrite tool can apply mechanical changes, but it is unavailable. Manual review and incremental fixes are required.

If you want, I can:
- Run the compile step locally in this environment if you provide a path to an installed JDK 21 (or allow me to install one).
- Add a simple `build.ps1` that automates compilation and run.
- Convert the project to use Maven or Gradle to manage Java versions and reproducible builds.
