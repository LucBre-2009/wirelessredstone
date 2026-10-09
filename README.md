# Wireless Redstone

A Fabric mod for Minecraft Java 26.3.

## Current prototype

- Adds a **Wireless Sender** and **Wireless Receiver** block.
- The sender detects a redstone input from adjacent vanilla redstone components.
- A receiver emits a vanilla redstone signal (strength 15) while it finds a powered sender on the same channel within its configured radius.
- Right-click a sender or receiver to cycle through 16 channel choices (`default`, `channel-2` ... `channel-16`).
- Shift-right-click to cycle range: 4, 8, and 16 blocks.
- Channel and range settings are saved with the block entity.

This is an initial gameplay prototype. It does not yet include a text-entry GUI for arbitrary channel names, custom block textures, or an in-game configuration screen. Signal updates are polled by receivers, so very large numbers of receivers may affect performance.

## Build

Requires Java 25.

```bash
./gradlew build
```

The mod JAR is produced in `build/libs/`.

## Install

Copy `WirelessRedstone-0.1.0.jar` from `build/libs/` into the `mods` folder of a Minecraft 26.3 Fabric client/server. Fabric API must also be installed.
