# VoidedClient protocol v1

Transport is an ordinary Minecraft custom payload. VoidedCore registers `voidedcore:client` on modern servers and `VC|Client` as a legacy fallback.

Client requests remain capped at 512 bytes. Server responses are capped at 4096 bytes so bounded leaderboard and server-browser state fits without unbounded payloads.

The client sends requests only. VoidedCore calculates and validates authoritative state.

## Core handshake

- `VC1|HELLO|<loader>|<modVersion>|<minecraftVersion>|<capabilities>`
- server -> client: `VC1|PROBE|<coreVersion>`
- server -> client: `VC1|WELCOME|<coreVersion>|<advertised-actions>`

## Existing RPG/exploration actions

- `VC1|ACTION|mending.repair`
- `VC1|ACTION|rpg.stats`
- `VC1|ACTION|rpg.stat.spend|<stat>|<amount>`
- `VC1|ACTION|rpg.stat.respec`
- `VC1|ACTION|rpg.skill.cast|dash|dash:yaw,pitch,strafe,forward`
- `VC1|ACTION|exploration.journal`
- `VC1|ACTION|exploration.contract`

RPG stats, HUD state, exploration discoveries, kill totals and contracts remain server-authoritative.

## Wave 4A companion dashboard (1.9.0)

Clients advertise `companion-home-v1,leaderboards-v1,linked-accounts-v1,server-navigation-v1`.

- `VC1|ACTION|companion.home`
- `VC1|COMPANION_HOME|player|server|networkOnline|balance|rpgLevel|discordLinked`
- `VC1|ACTION|leaderboard.request|balance|1`
- `VC1|LEADERBOARD|category|page|pages|selfRank|rank~name~value;...`
- `VC1|ACTION|accounts.request`
- `VC1|LINKED_ACCOUNTS|minecraftName|discordLinked|discordName|linkCode`
- `VC1|ACTION|accounts.link`
- `VC1|ACTION|accounts.unlink`
- `VC1|ACTION|server.list`
- `VC1|SERVER_LIST|id~name~online~current;...`
- `VC1|ACTION|server.switch|serverId`

VoidedCore 6.52.1 independently validates capabilities, feature configuration, permissions, leaderboard pages and server destinations. Hidden selector backends are not exposed through the companion browser. No Discord ID, token, website session, IP address or authentication secret is transmitted.


## Wave 4B Nether companion (1.10.0)

Capability: `nether-companion-v1`

Client action: `VC1|ACTION|nether.companion`

Server response:
`VC1|NETHER_COMPANION|level|xpIntoLevel|xpNeeded|kills|variantKills|ores|variantDiscoveries|landmarks|landmarkTotal|contract|contractProgress|contractTarget|contractComplete|bossState|bossRespawnSeconds`

The server owns every value in this payload. The client only renders it.

## Wave 4C End companion (1.11.0)

Capability: `end-companion-v1`

Client action: `end.companion`

Server response:
`VC1|END_COMPANION|level|xp|needed|kills|variantKills|nodes|variantDiscoveries|contract|contractProgress|contractTarget|contractComplete|eventState|eventRemaining`

The payload is display-only. End Attunement, contracts, events, drops and ritual requirements remain server-authoritative.


### Endgame Companion

`VC1|ENDGAME_COMPANION|unlocked|ascension|marks|trialProgress|trialTarget|trialClaimed|lifetimeTrials|totalClears|standardClears|eliteClears|mythicClears|bestAscension|bestMode|gauntletActive|activeMode|activeWave|activeTotalWaves|remainingSeconds|completionPercent|completionComplete`

Capability: `endgame-companion-v1`  
Action: `endgame.companion`


### Server context

`VC1|SERVER_CONTEXT|serverId|serverType|edition|rpgEnabled|gameplayEnabled`

Capability: `server-context-v1`. The client treats backend context as unknown/disabled during a Velocity switch until the destination backend sends a fresh context packet.


## Missions Companion

Client capability:
`missions-v1`

Client action:
`VC1|ACTION|missions.request`

Server state:
`VC1|MISSIONS|date|earnedToday|id~name~description~progress~target~reward~complete;...`

Mission progress and rewards are always server-authoritative.


## Wave 5E Market Companion

Capability:
`market-companion-v1`

Actions:
`VC1|ACTION|market.companion`
`VC1|ACTION|market.open`

Snapshot:
`VC1|MARKET_COMPANION|networkListings|ownListings|buyListings|sellListings|received|spent|fees|transactions|type~item~amount~price~server;...`

Market state is informational. Physical VoidedCore shops/chests remain authoritative.
