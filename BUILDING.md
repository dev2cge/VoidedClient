# Building VoidedClient 1.7.1

VoidedNetwork now treats Minecraft 26.2 as the primary client target. Older protocol clients may still join the server through the network compatibility layer, but VoidedClient itself is built for the native 26.2 client APIs.

## Required toolchain
- JDK 25 for the Gradle JVM and compiler.
- Gradle wrapper 9.5.1.
- Fabric: Loom 1.17.20, Loader 0.19.4, Fabric API 0.152.2+26.2.
- Forge: Forge 26.2-65.1.3 / ForgeGradle 7.

In IntelliJ set **Gradle distribution = Wrapper** and **Gradle JVM = JDK 25**.

### Upgrading from VoidedClient 1.0.1
Your old wrapper is Gradle 8.10.2 and cannot build Minecraft 26.2. Run `UPGRADE-TO-26.2.bat` once from the project root. It creates a clean temporary Gradle 9.5.1 wrapper and copies it into the project without loading Loom through the old wrapper.

## Build Fabric
```bat
gradlew :fabric:clean :fabric:build
```
Output: `fabric/build/libs/VoidedClient-Fabric-26.2-1.7.1.jar`

## Build Forge
```bat
gradlew :forge:clean :forge:build
```
Output: `forge/build/libs/VoidedClient-Forge-26.2-1.7.1.jar`

## Important
Minecraft 26.2 moved Fabric development to Mojang's unobfuscated names, Loom 1.17+, Gradle 9.5.x and Java 25. Do not reuse the 1.0.1 Gradle 8.10.2 / Java 21 wrapper for this release.

## Reproducible release build

Use a clean checkout and the committed wrapper:

```bash
./gradlew --no-daemon clean :fabric:build :forge:build
bash scripts/create-checksums.sh
```

Archive tasks strip timestamps and use deterministic entry ordering. The CI release workflow performs the same commands on a clean runner and publishes the resulting JARs, `SHA256SUMS.txt` and a GitHub build-provenance attestation.

Do not publish locally modified JARs under an official filename. Official artifacts must come from a signed/tagged source revision and match the published checksum.
