# Verifying a VoidedClient release

Download VoidedClient only from an official VoidedNetwork release location. Every official release should include the Fabric and/or Forge JAR, `SHA256SUMS.txt`, a source tag and build provenance.

## Check the SHA-256 hash

### Windows PowerShell

```powershell
Get-FileHash .\VoidedClient-Fabric-26.2-1.7.1.jar -Algorithm SHA256
Get-Content .\SHA256SUMS.txt
```

### Linux or macOS

```bash
sha256sum -c SHA256SUMS.txt
```

The result must exactly match the filename and hash published with that release. A mismatch means the file is not the published artifact; do not run it.

## Verify build provenance

With GitHub CLI installed and authenticated:

```bash
gh attestation verify VoidedClient-Fabric-26.2-1.7.1.jar --repo dev2cge/VoidedClient
```

A valid result ties the artifact to the official [`dev2cge/VoidedClient`](https://github.com/dev2cge/VoidedClient) source commit and release workflow.

## Rebuild from source

Use JDK 25 and the committed Gradle 9.5.1 wrapper:

```bash
git checkout client-v1.7.1
./gradlew --no-daemon clean :fabric:build :forge:build
bash scripts/create-checksums.sh
```

Compare the resulting hashes with the published file. The project removes archive timestamps and uses deterministic entry ordering. Differences should be investigated rather than dismissed.

## Malware-scanner results

The release checklist requires maintainers to publish a VirusTotal result link for each final JAR. Scanner labels are supporting evidence, not a replacement for source, checksum and provenance verification. Never weaken or repack a JAR merely to suppress a false positive; document and dispute the detection instead.
