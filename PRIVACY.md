# VoidedClient privacy notice

Last updated: 18 September 2026

## Summary

VoidedClient does not contain telemetry, advertising, analytics or external web requests. It communicates only with the Minecraft server the player has joined, using the documented `voidedcore:client` custom-payload channel.

## Data sent to the joined Minecraft server

| Event | Data |
| --- | --- |
| Connection handshake | Mod loader (`fabric` or `forge`), VoidedClient version, Minecraft version and supported feature names. |
| Keybind action | A fixed action identifier such as RPG stats, stored-XP repair or skill cast. |
| Stat allocation | The selected RPG stat and requested point amount. |
| Respec | A fixed respec request with no additional personal data. |
| Dash | Skill identifier plus the player's current horizontal movement vector. VoidedCore clamps and validates it. |

The server already receives ordinary Minecraft account, connection and gameplay data independently of VoidedClient. This notice describes only additional data introduced by the mod.

## Data received from VoidedCore

- VoidedCore/version handshake information.
- Server-calculated RPG level, XP, stats, equipment/effect contributions and derived bonuses.
- Live health, Defence, Strength, mana and skill cooldown display values.
- A successful-skill visual effect identifier.

These packets update the interface only. The server remains authoritative.

## Local storage

Fabric creates `config/voidedclient-rpg-hud.properties`. It contains only the chosen screen anchor for health, Strength, Defence, mana and skills. Deleting the file resets the layout. Forge 1.7.1 does not create a VoidedClient configuration file.

Minecraft itself stores changed keybinds in its standard options file. VoidedClient does not separately copy or transmit that file.

## Data not collected by VoidedClient

- Passwords or authentication tokens
- Microsoft/Minecraft launcher session data
- Discord tokens, accounts or messages
- Browser history, cookies or saved passwords
- Hardware identifiers or device fingerprints
- Files outside the HUD configuration described above
- Chat messages, screenshots, microphone or camera data
- Analytics, advertising IDs or usage profiles

## Scope

This notice covers the VoidedClient source in this repository. Minecraft, Fabric, Forge, Lunar Client, launchers, servers and other installed mods have their own behaviour and policies.
