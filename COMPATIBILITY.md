# Compatibility

## 1.10.0 Wave 4B Nether companion

- Designed for VoidedCore 6.55.0 and VoidedWorldGen 0.26.0.
- Fabric and Forge adapters advertise `nether-companion-v1`.
- Older servers simply ignore the capability and the rest of the client remains usable.
- Nether gameplay remains fully usable without VoidedClient through `/nether`.

# Compatibility

## 1.9.0 Wave 4A companion

- Requires VoidedCore 6.52.1 for the completed native companion dashboard behavior.
- VoidedClient 1.8.3 remains the completed pre-Wave-4A baseline and continues to work with existing RPG/Exploration fallbacks.
- Fabric and Forge advertise companion capabilities independently; VoidedCore only sends supported native sections.
- Server navigation remains permission- and destination-validated by Core, and hidden selector backends are not exposed.
- The client remains optional and does not use mixins, coremods or Lunar-private hooks.

## 1.8.2 Wave 3 completion

- Requires VoidedCore 6.50.0 for the native Daily Expedition contract packet and final floor-safe/vertical Dash rules.
- Older Core releases ignore the new contract action; the rest of the optional client remains safe.
- Fabric and Forge advertise the contract capability separately so Core only sends the new packet to supported builds.

## 1.8.1 WASD Dash and exploration journal

- Dash combines validated yaw/pitch with W, A, S and D input. VoidedCore 6.50.0 applies pitch to every movement direction, preserves stationary vertical intent and prevents downward floor collisions.
- Fabric and Forge can request the native server-authoritative Exploration Journal with K.
- VoidedCore 6.49.1 is required for the new directional payload and journal packet. Older client/Core combinations retain their existing fallbacks.

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
