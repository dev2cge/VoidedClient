# VoidedClient 1.9.0 — Wave 4A Companion

VoidedClient 1.9.0 is the Wave 4A QoL/server-companion release for Minecraft 26.2. The latest completed pre-Wave-4A baseline is 1.8.3.

## Companion dashboard

Press **U** to open the unified Voided Network dashboard. It provides server-authoritative profile highlights, network-wide online presence, paginated balance/playtime/RPG leaderboards, Minecraft and Discord link status, a paginated interactive server browser, and shortcuts into the existing Exploration and RPG interfaces.

The client never receives Discord credentials, website sessions, authentication tokens or authoritative economy/RPG state that it can modify. VoidedCore validates every request and remains authoritative. Players without the mod retain `/companion` and existing chat/command fallbacks.

## Existing RPG and exploration QoL

- **J** — RPG Stats
- **H** — Move/configure the RPG HUD on Fabric
- **K** — Exploration Journal and Daily Expedition access
- **R** — Stored-XP Mending repair action
- **Z/X/C/V/B** — Power Strike, Bulwark, Second Wind, Dash and Arcane Surge

Dash continues to send only yaw/pitch and local WASD intent; VoidedCore owns validation and final movement.

See `PROTOCOL.md`, `COMPATIBILITY.md` and `WAVE-ROADMAP.md` for details.
