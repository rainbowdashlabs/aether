# Aether

[![Maven Central](https://img.shields.io/maven-central/v/dev.chojo.aether/aether?style=for-the-badge&label=Release)](https://central.sonatype.com/artifact/dev.chojo.aether/aether)
[![Snapshot](https://img.shields.io/nexus/s/dev.chojo.aether/aether?server=https%3A%2F%2Feldonexus.de&style=for-the-badge&label=Snapshot&color=orange)](https://eldonexus.de/#browse/browse:maven-snapshots:dev%2Fchojo%2Faether)

Building blocks for Discord bots on [JDA](https://github.com/discord-jda/JDA) and
[jda-commands](https://github.com/Kaktushose/jda-commands): subscriptions and access checks for commands, Discord
and Ko-fi purchases, verified mail addresses, Discord login for Javalin web apps and JSON views of Discord objects.
You bring the storage; Aether brings the logic.

**Documentation: [docs.chojo.dev/aether](https://docs.chojo.dev/aether)**

## Requirements

- Java 25 or newer
- JDA 6 and jda-commands 5

## Dependency

Gradle (Kotlin DSL):

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("dev.chojo.aether:supporter:2.0.0")
}
```

Maven:

```xml
<dependency>
    <groupId>dev.chojo.aether</groupId>
    <artifactId>supporter</artifactId>
    <version>2.0.0</version>
</dependency>
```

The modules are `common`, `common-web`, `discord-oauth`, `kofi`, `mailing`, `serialization` and `supporter`.
`dev.chojo.aether:aether` brings all of them. Snapshots are on EldoNexus:
`https://eldonexus.de/repository/maven-snapshots/`.

## Contributing and releases

See [CONTRIBUTING.md](CONTRIBUTING.md). Versions come from publishdata: a `vX.Y.Z` tag publishes the release, `main`
publishes snapshots.

## License

LGPL-3.0-or-later, see [LICENSE.md](LICENSE.md).
