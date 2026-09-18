# VoidedClient 1.3.0 — RPG Interface

- Adds a native Minecraft 26.2 RPG character screen for Fabric/Lunar Fabric users.
- `J` now requests authoritative progression state from VoidedCore and opens the character screen instead of printing stats to chat when the server advertises the UI capability.
- Displays RPG level, XP progress, unspent points, all five progression stats, and their live gameplay bonuses.
- Adds +1, +5 and MAX stat allocation buttons. Every allocation is only a request; VoidedCore validates and persists it server-side before returning refreshed state.
- Extends the handshake with capability negotiation (`rpg-ui`, `rpg-stat-spend`) so unsupported loaders/versions automatically retain the vanilla chat fallback.
- Forge 26.2 remains supported for the existing keybind companion path; RPG GUI capability is not advertised until the Forge clientbound payload adapter is verified.
- No Mixins, coremods, bytecode transformers, Lunar-private hooks, or raw graphics APIs were introduced.
