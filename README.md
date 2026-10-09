# Wireless Redstone — Fabric 26.3 project scaffold

This is a **development scaffold**, not a finished wireless-redstone mod.
It currently registers two placeable blocks:

- `wirelessredstone:wireless_sender`
- `wirelessredstone:wireless_link`

The frequency system, range checks, redstone output logic, networking, and screen UI are **not implemented yet**. Do not install this as if those features already work.

## Build on Windows

1. Install a Java 25 JDK and make sure `java -version` uses Java 25 or newer.
2. Extract this ZIP to a normal folder.
3. Double-click `build.bat`.
4. On the first run, the script downloads Gradle 9.6.0 and Gradle downloads the Minecraft/Fabric build dependencies. An internet connection is required.
5. If the build succeeds, the JAR is copied into `output`.

No IDE, Python, Docker, or manually installed Gradle is required. Java 25 and internet access are required.

## Client and server

Loom combines the common and client source sets into one mod JAR. The same JAR is intended for the Fabric client and Fabric server. The current scaffold has not yet been compiled or tested in this environment.

## Version targets

- Minecraft Java: 26.3
- Fabric Loader: 0.19.5
- Fabric Loom: 1.17
- Gradle: 9.6.0
- Java: 25+
