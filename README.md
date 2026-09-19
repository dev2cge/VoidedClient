# VoidedClient 1.8.0

Optional VoidedNetwork client companion for Minecraft 26.2.

## RPG interface

On Fabric 26.2 (including Lunar's supported Fabric mod environment), press **J** to request server-authoritative RPG state from VoidedCore 6.41.0. The character screen shows level/XP, live mana and regeneration, base/equipment/enchant/temporary stat breakdowns, active gear/effect counts, hybrid build identity and power, derived bonuses, caps, allocation controls and respec state.

Version 1.8.0 separates health, Strength, Defence, mana and cooldowns into independent low-profile HUD elements. Press **H** to place each element beside/above the hotbar, in a screen corner, or hide it. Layout choices persist between launches. Skill activations receive an enhanced client flash, and Dash sends a yaw/pitch snapshot so the server can launch the player in the direction they are facing.

Default skill keys are **Z** Power Strike, **X** Bulwark, **C** Second Wind, **V** Dash and **B** Arcane Surge. Every key can be changed in Minecraft Controls.

Every spend/respec action is validated by VoidedCore. Players without VoidedClient lose no gameplay: `/rpgstats` continues to render the same progression information in chat.

## Keybinds

- **J** — View RPG Stats
- **H** — Move/configure RPG HUD elements
- **R** — Stored-XP Mending repair action
- **Z** — Power Strike
- **X** — Bulwark
- **C** — Second Wind
- **V** — Dash
- **B** — Arcane Surge

See `PROTOCOL.md` and `COMPATIBILITY.md` for protocol/loader details.
