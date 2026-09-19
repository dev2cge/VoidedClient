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

## Active skills, HUD and exploration journal (1.8.1)

Capable clients advertise `rpg-hud,rpg-skills,rpg-skill-fx`. Skill casts are requests; Dash includes the client's current yaw, pitch and normalized local movement input:

`VC1|ACTION|rpg.skill.cast|dash|dash:yaw,pitch,strafe,forward`

`strafe` is positive for left and negative for right. `forward` is positive for forward and negative for backward. VoidedCore validates the camera snapshot against the server-known view, clamps input, and calculates the world-space direction itself. VoidedClient 1.8.0 `look:yaw,pitch` remains accepted.

`VC1|RPG_HUD|health|maxHealth|strength|defence|mana|maxMana|defenceReduction|powerStrikeCooldown|bulwarkCooldown|secondWindCooldown|dashCooldown|arcaneSurgeCooldown`

`strength`, `defence`, health and mana values are final server-authoritative live totals. They already include valid held equipment, correctly worn armour, custom enchants, allocated attributes and temporary effects; the client must never recalculate equipment bonuses.

Cooldown values are whole seconds remaining; zero means ready. Clients must never calculate or mutate authoritative RPG state from the HUD packet.

After a successful cast, capable clients may receive `VC1|RPG_FX|skill-id`. This is presentation-only; vanilla-visible effects and every gameplay consequence remain server-authoritative.

Clients advertising `exploration-journal` may request the journal with `VC1|ACTION|exploration.journal`. Core responds with:

`VC1|EXPLORATION_JOURNAL|totalKills|highestRarityTier|contractName|contractProgress|contractTarget|contractComplete|id,name,discovered,kills;...`

The client only presents this state. Discovery flags, per-creature kills and contract progress are stored and calculated by VoidedCore.
<<<<<<< HEAD

## Daily expedition contract (1.8.2)

Clients advertise `exploration-contract` and request the view with `VC1|ACTION|exploration.contract`.

The server replies with:

`VC1|EXPLORATION_CONTRACT|type|name|description|progress|target|complete|rewardXp|rewardItem|rewardAmount|minimumTier|date`

All progression and rewards remain server-authoritative. The client only renders the supplied state.
=======
>>>>>>> 3b7f1f883f5e96a10d2589a168b920d9c11e9734
