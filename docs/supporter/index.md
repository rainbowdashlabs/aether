---
title: Supporter
sidebar_position: 1
---

# Supporter

The `supporter` module decides who may use which part of your bot. It knows your subscriptions, the features they
unlock and the commands that need them, and it checks every interaction before jda-commands runs it.

```kotlin
implementation("dev.chojo.aether:supporter:2.0.0")
```

## The pieces

| Term | Meaning |
|---|---|
| Subscription | Something a user or guild can buy, for example "Supporter". It has a numeric `id` and the ids it has on each platform. |
| Platform | Where it was bought: `Platform.DISCORD`, `Platform.KOFI` or `Platform.PATREON`. |
| Purchase type | `PurchaseType.SUBSCRIPTION` (recurring) or `PurchaseType.LIFETIME` (bought once). |
| Feature | Something your bot offers that needs a subscription, for example a custom profile. |
| Interaction rule | A command that needs a subscription, by its full command name. |
| Subscription context | The set of ids a user or guild has right now. Access checks compare it with the ids a feature or command needs. |

A feature or command is open to everyone when its list of ids is empty. Otherwise the subscription context must
contain one of the listed ids.

## What you provide

Aether stores nothing itself. You implement:

| Type | Purpose |
|---|---|
| `SubcriptionContextProvider` | Returns the subscription context of a user and of a guild, usually from your database. |
| `ISupporterErrorSupplier` | The message a user sees when a command is locked. |
| `ISubscriptions` | The subscriptions of one guild, with add, delete and clear. Used by the entitlement and Ko-fi services. |
| `ADiscordPurchase` and `AEntitlementService` | Only for Discord purchases. See [Discord entitlements](./discord-entitlements.md). |

## Where to go next

1. [Configuration](./configuration.md): describe your subscriptions, features and locked commands.
2. [Access checks](./access-checks.md): plug the middleware into jda-commands and check features in your commands.
3. [Discord entitlements](./discord-entitlements.md): turn Discord purchases into subscriptions.
4. [Ko-fi](../kofi.md): turn Ko-fi purchases into subscriptions.
