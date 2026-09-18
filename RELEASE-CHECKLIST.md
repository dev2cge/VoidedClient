# Official release checklist

- [ ] Release commit reviewed and tagged `client-vX.Y.Z`.
- [ ] Privacy, protocol, dependency and compatibility documents match the source.
- [ ] Public build workflow passes on the release tag.
- [ ] Dependency review has no unresolved high-severity additions.
- [ ] Fabric and Forge JARs come directly from the CI artifact.
- [ ] `SHA256SUMS.txt` is attached without modification.
- [ ] Build-provenance attestations exist for every released JAR and checksum file.
- [ ] Final JARs are submitted to VirusTotal and result links are added to release notes.
- [ ] Release is published through the official source page, Modrinth and CurseForge where applicable.
- [ ] Website/download page links to source, privacy, protocol, checksums and verification instructions.
- [ ] Discord announcement links to the official download page rather than attaching a second copy.
- [ ] Old official artifacts remain immutable; corrections use a new version.
