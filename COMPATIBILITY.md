# Compatibility

## 1.8.0 facing-direction Dash

- Dash sends a yaw/pitch snapshot rather than sampled velocity.
- VoidedCore validates the snapshot against its server-known view and owns the resulting movement.
- Older Core builds still receive the cast request but require the matching 6.47.0 server update for the new payload.

## 1.7.0 movable HUD and skill presentation

Fabric 26.2 can independently place health, Strength, Defence, mana and cooldown elements around the hotbar or screen corners. It also advertises the presentation-only `rpg-skill-fx` capability and sends its current horizontal movement vector with Dash. VoidedCore clamps/normalizes the hint and remains authoritative.

## 1.6.0 active skills and HUD

Fabric 26.2 advertises the live RPG HUD and active-skill capabilities. The server pushes only authoritative display state; all casts, mana costs and cooldowns remain validated in VoidedCore. Forge exposes the configurable skill keybinds while retaining its existing chat fallback for the full RPG stats interface.

## 1.5.1 interface rendering

The RPG character screen now repaints its interactive controls above the stat-row surfaces. This avoids dark or unreadable buttons under resource packs and preserves the same server-authoritative `rpg-stats-v3` protocol used by 1.5.0.

## Primary target
- Minecraft Java 26.2
- Fabric 26.2
- Forge 26.2
- Lunar Client when using a supported 26.2 Fabric profile/add-on path

VoidedClient does not use mixins, coremods, bytecode transformers, raw OpenGL hooks, or Lunar-private classes. It only registers a normal key mapping and a small custom-payload transport, reducing conflicts with other client mods.

## Older clients
VoidedNetwork may allow older Minecraft protocol versions to connect server-side, but the VoidedClient 1.1.x binaries are intentionally native 26.2 builds. The client companion is optional; older clients retain server-side commands/interaction fallbacks.


## 1.5.0 RPG UI capability

Fabric 26.2 advertises `rpg-ui,rpg-stat-spend,rpg-stat-respec,rpg-stats-v2,rpg-stats-v3` and receives the full mana/equipment/effect RPG state from VoidedCore 6.38.0. Core retains V2 and legacy state for older clients. Forge 26.2 deliberately does not advertise the UI capability yet, so Core falls back to chat for `/rpgstats` and the J action instead of sending an unsupported clientbound UI payload.
