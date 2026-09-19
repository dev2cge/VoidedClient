# VoidedClient 1.8.2

Optional VoidedNetwork client companion for Minecraft 26.2.

## RPG interface

On Fabric 26.2 (including Lunar's supported Fabric mod environment), press **J** to request server-authoritative RPG state from VoidedCore 6.41.0. The character screen shows level/XP, live mana and regeneration, base/equipment/enchant/temporary stat breakdowns, active gear/effect counts, hybrid build identity and power, derived bonuses, caps, allocation controls and respec state.

Version 1.8.2 completes the Wave 3 client experience. Press **K** to view the Exploration Journal, then open the full Daily Expedition screen for its objective, live progress, reward and reset date. Defeated creatures are revealed in colour with their kill count, while undiscovered creatures remain blacked out. The server remains authoritative for every discovery and contract.

Dash now sends yaw, pitch and current WASD intent. VoidedCore 6.50.0 validates that input and supports movement-relative angled dashes, a stationary vertical dash while looking up, and safe forward redirection when looking down over a solid floor.

The movable low-profile RPG HUD remains available on Fabric with **H**. Dash now combines the camera direction with live WASD input, allowing forward, backward, sideways and diagonal dashes while retaining pitch only for forward movement.

Default skill keys are **Z** Power Strike, **X** Bulwark, **C** Second Wind, **V** Dash and **B** Arcane Surge. Every key can be changed in Minecraft Controls.

Every spend/respec action is validated by VoidedCore. Players without VoidedClient lose no gameplay: `/rpgstats` continues to render the same progression information in chat.

## Keybinds

- **J** — View RPG Stats
- **H** — Move/configure RPG HUD elements
- **K** — Open Exploration Journal
- **R** — Stored-XP Mending repair action
- **Z** — Power Strike
- **X** — Bulwark
- **C** — Second Wind
- **V** — Dash
- **B** — Arcane Surge

See `PROTOCOL.md` and `COMPATIBILITY.md` for protocol/loader details.
