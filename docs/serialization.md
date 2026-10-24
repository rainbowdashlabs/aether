---
title: Serialization
sidebar_position: 7
---

# Serialization

The `serialization` module turns JDA objects into small JSON objects, for example to show a guild's roles and
channels in a web dashboard.

```kotlin
implementation("dev.chojo.aether:serialization:2.0.0")
```

## Setup

Add `DiscordSerializationModule` to your Jackson 3 mapper:

```java
JsonMapper mapper = JsonMapper.builder()
        .addModule(new DiscordSerializationModule())
        .build();

String json = mapper.writeValueAsString(guild);
```

To use it for `ctx.json(...)` in Javalin, wrap this mapper in your own Javalin `JsonMapper` implementation.

## What you get

| JDA type | JSON fields |
|---|---|
| `Guild` | `name`, `id`, `iconUrl`, `highestBotRole`, `roles`, `channels`, `reactions` |
| `Role` | `name`, `id`, `position`, `color` |
| `Member` | `displayName`, `id`, `color`, `profilePictureUrl` |
| `GuildChannel` | `name`, `id`, `type`, `visible` |
| `Category` | `name`, `id`, `channels` |
| `RichCustomEmoji` | `name`, `id`, `url` |
| `DiscordLocale` | `code`, `name`, `nativeName`, `internalName` |

Details:

- Colours are hex strings like `#ff0000`. A role or member without colour is `#ffffff`.
- A guild lists its roles without `@everyone` and without roles managed by integrations. `highestBotRole` is the
  highest role of your bot.
- A guild's `channels` holds `channels`, the channels outside any category, and `categories`, each with its own
  channels.
- `visible` tells whether your bot can see the channel.
- A guild's `reactions` are its custom emojis. They are loaded from Discord while the guild is written, so writing a
  guild makes a blocking request.
