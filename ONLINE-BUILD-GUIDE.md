# Online build with GitHub Codespaces

## What this package is
This is a development scaffold for Minecraft Java 26.3 + Fabric, not a finished wireless-redstone mod. The sender/link blocks are only a starting point. Frequency management, range checks, redstone output logic, networking, and the configuration screen are not implemented yet. A successful compile does not mean those features work.

## Build in Codespaces
1. Sign in to GitHub.
2. Create a **private** repository (recommended if you want to keep the source private).
3. In the new repository, choose **Add file → Upload files** and upload the *contents* of this `project` folder. Ensure `.devcontainer/devcontainer.json` is included. Commit the files.
4. Open the repository's **Code** menu → **Codespaces** → **Create codespace on main**.
5. Wait for the container to finish creating. In the terminal, run:

   ```bash
   java -version
   gradle --version
   ```

   Java should report version 25 or newer.
6. In the terminal, from the repository root, create a Gradle wrapper pinned to the version for Minecraft 26.3:

   ```bash
   gradle wrapper --gradle-version 9.6.0
   ```

7. Build the project:

   ```bash
   ./gradlew build
   ```

8. If the build succeeds, open `build/libs` in the Explorer. Download the JAR with the shortest name (not `-sources.jar` and not a `-dev` JAR). If desired, use the Explorer context menu to download it.

## If the build fails
- Read the first `FAILURE:` / `What went wrong:` message in the terminal.
- If it says a plugin/dependency cannot be found, check that Codespaces has internet access and rerun `./gradlew build --refresh-dependencies`.
- If it reports Java version issues, check `java -version`; the dev container feature should install Java 25.
- If it reports missing classes/methods in the Java source, that is a source/API compatibility issue, not a Java installation issue. Copy the complete error text for diagnosis.

## Minecraft 26.3 notes
Fabric's current guidance for 26.3 lists Java 25, Loom 1.17, Gradle 9.6.0 and Fabric Loader 0.19.5. This scaffold sets these versions. The current code has not been successfully built or tested in this package, and the wireless features are not implemented.

## Installing the eventual mod
For a mod with both common/server logic and client UI, one JAR may be used on both sides if the mod is packaged correctly. Put the final JAR in the `mods` folder on the Fabric server and on each player's client. Back up the world before testing.
