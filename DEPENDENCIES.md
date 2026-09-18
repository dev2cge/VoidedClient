# Runtime and build dependencies

VoidedClient 1.7.1 intentionally keeps its dependency surface small.

| Component | Version | Purpose | Included in mod JAR |
| --- | --- | --- | --- |
| Minecraft Java | 26.2 | Target game/client API | No |
| Java | 25 | Required runtime/toolchain for Minecraft 26.2 | No |
| Gradle Wrapper | 9.5.1 | Repeatable build entrypoint | Wrapper bootstrap only |
| Fabric Loader | 0.19.4 | Fabric mod loading | No |
| Fabric API | 0.152.2+26.2 | Keybind, HUD and payload APIs | No |
| Fabric Loom | 1.17.20 | Fabric build tooling | No |
| Forge | 26.2-65.1.3 | Forge loader and client APIs | No |
| ForgeGradle | 7.0.17 | Forge build tooling | No |
| VoidedClient common module | 1.7.1 | Protocol and state parsing shared by loaders | Yes |

There is no HTTP, analytics, Discord, database, native-code or advertising dependency. Dependabot and the pull-request dependency-review workflow monitor changes to this list.
