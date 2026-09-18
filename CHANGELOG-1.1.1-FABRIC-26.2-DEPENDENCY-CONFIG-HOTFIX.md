# VoidedClient 1.1.1 — Fabric 26.2 Dependency Configuration Hotfix

- Fixed the Fabric 26.2 build failing with `Could not find method modImplementation()`.
- Minecraft 26.1+ uses unobfuscated Loom (`net.fabricmc.fabric-loom`), where Fabric Loader and Fabric API are declared with standard Gradle `implementation` configurations.
- Kept the native Minecraft 26.2 / Java 25 / Gradle 9.5.1 / Loom 1.17.20 target.
- No gameplay or protocol behavior changed.
