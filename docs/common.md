---
title: Common
sidebar_position: 2
---

# Common

The `common` module holds the parts every other module uses. You can use them in your bot as well.

```kotlin
implementation("dev.chojo.aether:common:2.0.0")
```

## Look up users

Several modules need to find a Discord user or member by id. They ask an `IUserProvider`, which you implement:

```java
public class JdaUserProvider implements IUserProvider {
    private final JDA jda;

    public JdaUserProvider(JDA jda) {
        this.jda = jda;
    }

    @Override
    public Optional<User> byId(long id) {
        return Optional.ofNullable(jda.retrieveUserById(id).complete());
    }

    @Override
    public Optional<User> byId(String id) {
        return byId(Long.parseLong(id));
    }

    @Override
    public Optional<Member> byId(long id, long guildId) {
        Guild guild = jda.getGuildById(guildId);
        return guild == null ? Optional.empty() : byId(id, guild);
    }

    @Override
    public Optional<Member> byId(long id, Guild guild) {
        return Optional.ofNullable(guild.retrieveMemberById(id).complete());
    }
}
```

The example blocks on JDA's REST calls. Use a cache if your bot looks up users often.

## Registries

A `Registry` holds a set of keys, for example the platforms or subscription types of the supporter module. A key
is anything that implements `Key`, which only has a `name()`.

```java
Registry<Platform> platforms = new Registry<>(Platform.class)
        .register(Platform.DISCORD)
        .register(Platform.KOFI);

Optional<Platform> kofi = platforms.byName("kofi");
```

- `register(key)` adds a key and returns the registry, so you can chain calls. Registering the same key twice
  throws an `IllegalArgumentException`.
- `byName(name)` finds a key by its name. Case does not matter.
- `entries()` returns all keys, `unregister(key)` removes one.

Registries matter when Aether reads keys from configuration files: a name that is not registered can not be read.

## Handle exceptions in one place

`InterceptingEventManager` is a JDA event manager that catches exceptions from your event listeners and from
jda-commands interactions and passes them to handlers you register.

JDA does not move listeners to a new event manager. Since jda-commands registers its own listener when it starts,
move the existing listeners over when you switch:

```java
JDACommands commands = JDACommands.builder(jda).start();

InterceptingEventManager eventManager = new InterceptingEventManager(commands);
List<Object> listeners = jda.getRegisteredListeners();
jda.setEventManager(eventManager);
listeners.forEach(jda::addEventListener);

eventManager.exception(InsufficientPermissionException.class, (err, context) ->
        log.warn("Missing permission in event {}", context.event(), err));
eventManager.exception(Throwable.class, (err, context) ->
        log.error("Unhandled exception in event {}", context.event(), err));
```

For each exception, the first handler whose class matches is called, in the order you registered them. Register
specific exception classes first and `Throwable` last. An exception that no handler matches is dropped without a log
entry.

## Autocomplete helpers

`Completion` builds the choices for an autocomplete reply from the text the user typed so far. It returns at most 25
choices, the limit Discord allows.

```java
List<Command.Choice> choices = Completion.complete(event.getFocusedOption().getValue(), "red", "green", "blue");
event.replyChoices(choices).queue();
```

- `complete(value, String...)`, `complete(value, Collection<String>)` and `complete(value, Stream<String>)` keep the
  entries that start with the typed text, ignoring case.
- `complete(value, inputs, mapping)` takes any objects and a function that turns them into text.
- `complete(value, MyEnum.class)` offers the enum constants in lower case. `complete(value, MyEnum.class, lowerCase,
  strip)` lets you keep the case or drop the underscores.

`Choice` turns strings, ints and doubles into `Command.Choice` objects. `Choice.builder()` builds a list of choices
with names that differ from their values.

## Small helpers

- `Colors.toHex(color)` turns a `java.awt.Color` into `#rrggbb`. `Colors.Pastel` and `Colors.Strong` hold two colour
  palettes.
- `Pair` and `Triple` hold two or three values in public final fields (`first`, `second`, `third`).
- `TriConsumer`, `QuadConsumer`, `TriFunction` and `QuadFunction` are consumers and functions with three or four
  arguments. `ThrowingConsumer` and `ThrowingFunction` may throw a checked exception.
