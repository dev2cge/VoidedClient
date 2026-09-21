# VoidedClient 1.10.0 — Network-Aware HUD

- Adds `SERVER_CONTEXT` backend identity support.
- Clears and hides the RPG HUD while on Hub/non-gameplay backends.
- Stops sending RPG stat/skill key actions when the active backend does not expose RPG gameplay.
- Clears stale RPG state immediately when a new backend sends its handshake probe.
- Keeps the public client version at 1.10.0.
