# VoidedClient 1.4.0 — RPG Stat System II

- Rebuilds the native character screen around the canonical **Strength, Defence, Vitality, Agility and Intelligence** model, including live derived-value breakdowns.
- Stat rows now surface mastery state and disable allocation controls at the hard cap.
- Adds server-authoritative RPG respec capability with a two-click client confirmation and cooldown display.
- Advertises `rpg-stat-respec` and `rpg-stats-v2` capabilities during the optional companion handshake while retaining the 1.3 packet parser for compatibility.
- Keeps the same J-key workflow and remains optional; vanilla/unsupported clients continue using Core's chat fallback.
