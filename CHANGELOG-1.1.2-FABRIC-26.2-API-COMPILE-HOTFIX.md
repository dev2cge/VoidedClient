# VoidedClient 1.1.2 — Fabric 26.2 API Compile Hotfix

## Fixed

- Migrated the Fabric key mapping import from `client.keybinding.v1.KeyBindingHelper` to the Minecraft 26.2 `client.keymapping.v1.KeyMappingHelper`.
- Uses `KeyMappingHelper.registerKeyMapping(...)` for the Repair Held Item binding.
- Migrated current-screen checks from the removed `Minecraft.screen` field to `Minecraft.gui.screen()`.
- Removed the Gradle 9 execution-time `Task.project` deprecation from Fabric resource expansion by resolving version values during configuration.

## Compatibility

- Minecraft 26.2
- Java 25
- Fabric Loader 0.19.4+
- Fabric API 0.152.2+26.2
- Fabric Loom 1.17.20
- Gradle wrapper 9.5.1
