# Wireless Redstone

A Fabric mod for Minecraft Java 26.3.

## Features

- Adds a compact **Wireless Sender** and **Wireless Receiver**.
- The sender detects a redstone input from adjacent vanilla redstone components.
- The receiver emits a vanilla redstone signal (strength 15) while it finds a powered sender with the same frequency in range.
- Right-click a sender or receiver to open a GUI and enter a numeric frequency from **0 to 256**. The frequency is saved with the block entity and no longer appears in chat.
- Shift-right-click to cycle the range: 1024 blocks, 2048 blocks, or unlimited within currently loaded chunks.
- The receiver's indicator changes from copper-colored to red when powered.
- Compact models use existing Minecraft textures (iron, copper, redstone block, and white wool). No external texture files or resource-pack dependency are needed.
- The frequency screen uses standard Minecraft widgets with a compact industrial/copper-redstone look.

Frequency and range are stored per block. Existing prototype data is migrated from previous channel names where possible.

Receivers check for active senders every 10 ticks using an index of loaded sender block entities. Unlimited range only considers senders in currently loaded chunks; it does not force-load distant chunks. Very large numbers of senders and receivers may still affect performance.

## Build

Requires Java 25.

```bash
./gradlew build
```

The mod JAR is produced in `build/libs/`.

## Install

Copy `WirelessRedstone-0.1.0.jar` from `build/libs/` into the `mods` folder of a Minecraft 26.3 Fabric client/server. Fabric API must also be installed.
