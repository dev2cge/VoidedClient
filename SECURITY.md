# Security policy

## Supported releases

Security fixes are provided for the latest VoidedClient 1.7.x release. Older binaries should be replaced rather than redistributed with unofficial patches.

## Reporting a vulnerability

Do not post an exploitable vulnerability, credential or private server detail in a public issue. Use the source repository's private security-advisory feature and include:

- affected VoidedClient version and loader;
- reproduction steps;
- expected and observed behaviour;
- impact;
- relevant logs with tokens, addresses and player identifiers removed.

Reports should receive an acknowledgement before public disclosure. A fixed release should include a changelog entry, fresh checksums and new build provenance.

## Runtime security boundaries

- VoidedCore validates every action, stat allocation, respec, mana cost and cooldown.
- Client-provided Dash movement is a hint and must remain clamped server-side.
- Incoming custom payloads are capped at 512 bytes before parsing.
- The client does not execute commands received through the custom payload.
- Unknown servers and unsupported payloads must not make the game unusable.
- New external networking, telemetry, process execution or credential access requires an explicit security review and privacy-document update.

## Release integrity

Official releases must pass the public build workflow, dependency review and source security audit. Publish only the CI artifacts and their generated `SHA256SUMS.txt`; do not rebuild and silently replace a release JAR.
