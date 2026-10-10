# Wireless Redstone

A Fabric mod for Minecraft Java 26.3.

## Features

- Adds a compact **Wireless Sender** and **Wireless Receiver**.
- Uses custom 16×16 pixel-art PNG textures: dark metal sender casing, copper coil, red signal core, light receiver casing, copper trim, and separate inactive/active receiver indicators.
- The sender detects a redstone input from adjacent vanilla redstone components.
- The receiver emits a vanilla redstone signal (strength 15) while it finds a powered sender with the same frequency in range.
- Right-click a sender or receiver to open a GUI and enter a numeric frequency from **0 to 256**. The frequency is saved with the block entity and no longer appears in chat.
- Shift-right-click to cycle the range: 1024 blocks, 2048 blocks, or unlimited within currently loaded chunks.
- The receiver's indicator changes from copper-colored to glowing red when powered.
- Item icons reuse their block models, so the new textures are visible in both the world and inventory.

Frequency and range are stored per block. Existing prototype data is migrated from previous channel names where possible.

Receivers check for active senders every 10 ticks using an index of loaded sender block entities. Unlimited range only considers senders in currently loaded chunks; it does not force-load distant chunks. Very large numbers of senders and receivers may still affect performance.

## Custom textures

The texture source generator is `tools/generate_textures.py`. It creates seven 16×16 PNG files in `src/main/resources/assets/wirelessredstone/textures/block/`.

GitHub Actions generates and commits those PNGs when the generator or its workflow changes. You can also run the generator locally after installing Pillow:

```bash
python -m pip install Pillow
python tools/generate_textures.py
```

## Build

Requires Java 25.

```bash
./gradlew clean build
```

The mod JAR is produced in `build/libs/`.

## Install

Copy `WirelessRedstone-0.1.0.jar` from `build/libs/` into the `mods` folder of a Minecraft 26.3 Fabric client/server. Fabric API must also be installed.
