# Why VoidedClient is safe to review

VoidedClient is an optional user-interface companion. VoidedCore remains authoritative for RPG attributes, equipment, mana, cooldowns, skill effects, damage and progression. Installing the mod does not grant gameplay authority and playing without it does not cause a kick, restriction or progression disadvantage.

The source is published for inspection and release verification under the proprietary terms in `LICENSE.md`. Source availability does not grant permission to reuse, modify or redistribute the project.

## Verifiable properties

| Area | Behaviour |
| --- | --- |
| External networking | None. The code contains no HTTP client, web socket, raw socket or external service integration. |
| Minecraft networking | One custom-payload channel: `voidedcore:client`. Payloads are UTF-8 and capped at 512 bytes. |
| Telemetry | None. There are no analytics, crash-upload or tracking endpoints. |
| Credentials | The mod does not request or read passwords, launcher sessions, Discord tokens, browser data or environment secrets. |
| Local files | Fabric stores only HUD anchors in `config/voidedclient-rpg-hud.properties`. Minecraft stores keybind choices through its normal options system. |
| Code modification | No mixins, coremods, bytecode transformers, native hooks or Lunar-private APIs. |
| Processes | The runtime does not launch commands, executables or child processes. |
| Privileges | The mod does not require administrator/root access. |
| Obfuscation | Official builds are not intentionally obfuscated. Class and package names remain inspectable. |

## How trust is established

1. The published source corresponds to a version tag.
2. Public CI builds the Fabric and Forge JARs from that clean tag.
3. CI publishes SHA-256 hashes beside the JARs.
4. GitHub build provenance binds release artifacts to the source workflow and commit.
5. Dependency review blocks newly introduced vulnerable dependencies on pull requests.
6. A source audit fails if runtime code introduces external networking, process execution, environment-secret access or known token patterns without an explicit security review.

This evidence is stronger than a claim that a file is “100% safe.” Users should verify the checksum, source tag and build provenance using `VERIFYING-RELEASES.md`.
