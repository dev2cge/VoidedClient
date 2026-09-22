# VoidedClient 1.11.0 — Missions Companion + Build Fix

- Bumped the client project from 1.10.0 to 1.11.0.
- Fabric and Forge HELLO packets now report client version 1.11.0.
- Fixed Fabric compilation by importing `MissionsState`.
- Fixed Forge compilation by importing `MissionsState`.
- Kept the `missions-v1` capability and native Missions Companion tab.
- Updated the root Gradle compatibility message to 1.11.0.
- Updated Forge `processResources` so it no longer accesses `project` during task execution, avoiding the Gradle 10 deprecation warning shown by CI.
- Minecraft target remains 26.2.
