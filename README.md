# VoidedClient 1.7.1

Optional VoidedNetwork client companion for Minecraft 26.2.

Official source and releases: <https://github.com/dev2cge/VoidedClient>

VoidedClient is optional, source-available and server-authoritative. Players without it retain the vanilla chat and command fallbacks and are never kicked or restricted for not installing it.

## Trust and safety

- No telemetry, analytics, advertising or external web requests.
- No Discord integration, token access, credential access or device fingerprinting.
- No mixins, coremods, bytecode transformers, process execution or Lunar-private hooks.
- One documented Minecraft custom-payload channel, capped at 512 bytes.
- One local Fabric file containing only HUD element positions.
- Public CI builds produce SHA-256 checksums and build-provenance attestations.
- Release archives are deterministic: timestamps are removed and file order is fixed.

Read [`TRUST.md`](TRUST.md), [`PRIVACY.md`](PRIVACY.md), [`SECURITY.md`](SECURITY.md), [`PROTOCOL.md`](PROTOCOL.md) and [`VERIFYING-RELEASES.md`](VERIFYING-RELEASES.md) before installing.

## Licence

VoidedClient is **source-available proprietary software**, not open-source software. Players may use unmodified official builds with VoidedNetwork, and reviewers may inspect or locally build the published source solely for security and release verification. Copying, modifying, redistributing, rebranding, selling or reusing the project in another mod, plugin, client or service is prohibited without prior written permission.

See [`LICENSE.md`](LICENSE.md) for the complete terms and [`THIRD-PARTY-NOTICES.md`](THIRD-PARTY-NOTICES.md) for components that retain their own licences.

## RPG interface

On Fabric 26.2 (including Lunar's supported Fabric mod environment), press **J** to request server-authoritative RPG state from VoidedCore 6.41.0. The character screen shows level/XP, live mana and regeneration, base/equipment/enchant/temporary stat breakdowns, active gear/effect counts, hybrid build identity and power, derived bonuses, caps, allocation controls and respec state.

Version 1.7.1 retains the 1.7.0 movable HUD and skill presentation while adding a verifiable release and security process. Press **H** to place each HUD element beside/above the hotbar, in a screen corner, or hide it. Layout choices persist between launches. Skill activations receive an enhanced client flash, and Dash sends the current horizontal movement vector for responsive strafing, backwards and diagonal dashes.

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

See `PROTOCOL.md` and `COMPATIBILITY.md` for protocol/loader details. Build and verification instructions are in `BUILDING.md` and `VERIFYING-RELEASES.md`.
