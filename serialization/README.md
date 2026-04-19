# Serialization Module

The serialization module provides Jackson serializers for [JDA](https://github.com/discord-jda/JDA) entities. 
It converts JDA entities into POJOs that can be easily serialized to JSON.

## Features

- Custom Jackson module: `DiscordSerializationModule`
- Serializers for:
    - `Guild` -> `GuildPOJO`
    - `Role` -> `RolePOJO`
    - `Member` -> `MemberPOJO`
    - `RichCustomEmoji` -> `ReactionPOJO`
    - `Category` -> `CategoryPOJO`
    - `GuildChannel` -> `ChannelPOJO`
    - `DiscordLocale` -> `LocalePOJO`

## Installation

### Gradle (Kotlin DSL)

```kotlin
implementation("dev.chojo.aether:serialization:version")
```

### Maven

```xml
<dependency>
    <groupId>dev.chojo.aether</groupId>
    <artifactId>serialization</artifactId>
    <version>version</version>
</dependency>
```

### Module Info

If you are using Java Modules, add the following to your `module-info.java`:

```java
module your.module {
    requires dev.chojo.aether.serialization;
}
```

## Usage

To use the serializers, register the `DiscordSerializationModule` with your Jackson `ObjectMapper`:

```java
import dev.chojo.aether.serialization.jackson.DiscordSerializationModule;
import tools.jackson.databind.ObjectMapper;

ObjectMapper mapper = new ObjectMapper();
mapper.registerModule(new DiscordSerializationModule());
```

Once registered, you can serialize JDA entities directly:

```java
Guild guild = ...; // Get JDA Guild instance
String json = mapper.writeValueAsString(guild);
```
