# VoidedClient 1.1.0 — Minecraft 26.2 Native Target

- Promoted Minecraft 26.2 to the primary VoidedClient target.
- Migrated Fabric from Yarn/1.21.1 to Minecraft 26.2 Mojang names.
- Migrated Fabric build tooling to Loom 1.17.20, Gradle 9.5.1 and Java 25.
- Updated Fabric networking to the 26.2 `CustomPacketPayload` / `StreamCodec` APIs.
- Updated custom key mapping registration to the typed 26.2 `KeyMapping.Category` API.
- Updated the Forge build target to Forge 26.2-65.1.3 / ForgeGradle 7.
- Kept the client optional so older protocol clients can still use server-side fallback interactions.
- No mixins, coremods or Lunar-private hooks were introduced.
