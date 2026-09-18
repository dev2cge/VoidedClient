# VoidedClient protocol v1

Transport is an ordinary Minecraft custom payload. VoidedCore registers `voidedcore:client` on modern servers and `VC|Client` as a legacy server fallback.

UTF-8 payloads are capped at 512 bytes:

- `VC1|HELLO|<loader>|<modVersion>|<minecraftVersion>`
- `VC1|ACTION|mending.repair`
- server -> client: `VC1|PROBE|<coreVersion>`
- server -> client: `VC1|WELCOME|<coreVersion>|<action>:<defaultKey>`

The client sends only an action request. VoidedCore calculates and validates all game state. New keybind actions can be added without changing the transport framing.

## RPG interface (1.5.0)

A client may append capabilities to HELLO:

`VC1|HELLO|fabric|1.5.0|26.2|rpg-ui,rpg-stat-spend,rpg-stat-respec,rpg-stats-v2,rpg-stats-v3`

Core only sends the native RPG state packet when `rpg-ui` is advertised:

`VC1|RPG_STATS_V2|level|xpInto|xpNeeded|points|strength|defence|vitality|agility|intelligence|damageBonus|knockbackBonus|defenceReductionPercent|healthBonus|healthRegenPerSecond|movementSpeedBonus|attackSpeedBonus|cooldownReduction|manaCapacity|ritualPower|skillPower|totalPoints|spentPoints|maxPointsPerStat|softCap|respecCooldownSeconds|buildName|buildScore`

Clients advertise `rpg-stats-v2` to receive this shape. Core retains the legacy `VC1|RPG_STATS|...` packet for VoidedClient 1.3 compatibility.

VoidedClient 1.5 advertises `rpg-stats-v3` and receives the V2 fields followed by current mana, mana regeneration, five equipment contributions, five temporary-effect contributions, equipped-piece count and active-effect count. Core continues sending V2 to 1.4 clients.

Stat changes are requests, never authoritative client state:

`VC1|ACTION|rpg.stat.spend|strength|1`

VoidedCore validates available points, stat caps and persistence, then sends a refreshed RPG_STATS payload. Clients without the UI capability receive the normal `/rpgstats` chat presentation.


Respec action:

`VC1|ACTION|rpg.stat.respec`

## Active skills and HUD (1.7.1)

Fabric 1.7 clients advertise `rpg-hud,rpg-skills,rpg-skill-fx`. Skill casts are requests; Dash may include a normalized horizontal movement hint:

`VC1|ACTION|rpg.skill.cast|dash|x,z`

VoidedCore validates the skill, mana, cooldown and player state before applying anything. It then refreshes this compact client-only display packet:

`VC1|RPG_HUD|health|maxHealth|strength|defence|mana|maxMana|defenceReduction|powerStrikeCooldown|bulwarkCooldown|secondWindCooldown|dashCooldown|arcaneSurgeCooldown`

`strength`, `defence`, health and mana values are final server-authoritative live totals. They already include valid held equipment, correctly worn armour, custom enchants, allocated attributes and temporary effects; the client must never recalculate equipment bonuses.

Cooldown values are whole seconds remaining; zero means ready. Clients must never calculate or mutate authoritative RPG state from the HUD packet.

After a successful cast, capable clients may receive `VC1|RPG_FX|skill-id`. This is presentation-only; vanilla-visible effects and every gameplay consequence remain server-authoritative.

## Privacy and transport guarantees

- The protocol never includes passwords, account tokens, Discord data, chat content, inventory contents, filesystem paths or hardware identifiers.
- Fabric and Forge send packets only through the active Minecraft server connection; VoidedClient has no external network client.
- Clientbound and serverbound VoidedClient payloads are capped at 512 bytes.
- Arguments are stripped of framing characters and line breaks and are length-limited before encoding.
- Unknown actions and all gameplay-relevant values are rejected or recalculated by VoidedCore.
- The protocol does not contain a remote-command or arbitrary-code execution message.

See `PRIVACY.md` for the complete data inventory.
