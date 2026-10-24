---
title: Discord entitlements
sidebar_position: 4
---

# Discord entitlements

When someone buys one of your app's SKUs in Discord, Discord creates an entitlement. `AEntitlementService` listens
for entitlement events, stores each purchase through your code and turns it into a subscription of a guild.

## Your purchase class

Extend `ADiscordPurchase` with the class you store in your database. It holds the user, SKU, entitlement type,
target (`SkuTarget.GUILD` or `SkuTarget.USER`), subscription id, entitlement id, expiry, whether it is persistent and
the guild it is assigned to (`0` for none). You implement:

| Method | What it should do |
|---|---|
| `renew(expiresAt)` | Store a new expiry. |
| `delete()` | Delete the purchase. |
| `assignToGuild(guildId)` | Store the guild and return `true` on success. |
| `unassignFromGuild()` | Set the guild back to `0` and return `true` on success. |

## Your service

Extend `AEntitlementService` and register it as a JDA event listener:

```java
public class EntitlementService extends AEntitlementService<DiscordPurchase> {
    private final PurchaseRepository repository;

    public EntitlementService(SupporterConfiguration<?, ?, ?> configuration, PurchaseRepository repository) {
        super(configuration);
        this.repository = repository;
    }

    @Override
    protected DiscordPurchase buildPurchase(long userId, long skuId, Entitlement.EntitlementType type,
            SkuTarget target, long subscriptionId, long entitlementId, @Nullable Instant expiresAt,
            long guildId, PurchaseType purchaseType) {
        return new DiscordPurchase(userId, skuId, type, target, subscriptionId, entitlementId, expiresAt,
                purchaseType.isLifetime(), guildId);
    }

    @Override
    protected DiscordPurchase registerPurchase(DiscordPurchase purchase) {
        return repository.insert(purchase);
    }

    @Override
    protected DiscordPurchase updatePurchase(DiscordPurchase purchase) {
        return repository.update(purchase);
    }

    @Override
    protected DiscordPurchase unregisterPurchase(DiscordPurchase purchase) {
        return repository.delete(purchase);
    }

    @Override
    protected ISubscriptions guildSubscriptions(long guildId) {
        return repository.guildSubscriptions(guildId);
    }

    @Override
    protected List<DiscordPurchase> getExpiredPurchases() {
        return repository.expiredAndAssigned();
    }
}

jda.addEventListener(entitlementService);
```

## What happens

| Event | Action |
|---|---|
| Entitlement created | The purchase is built and passed to `registerPurchase`. A guild purchase is enabled for its guild right away. |
| Entitlement updated | The purchase is passed to `updatePurchase` and enabled again for the guild it belongs to. |
| Entitlement deleted | The purchase is passed to `unregisterPurchase` and its subscription is removed from the guild. |

The service finds the subscription through the SKU id in the `DISCORD` platform ids of your
[configuration](./configuration.md#subscriptions). An unknown SKU is logged and ignored.

Every 60 minutes, starting 30 minutes after the service was created, it disables the subscriptions of all purchases
from `getExpiredPurchases`.

## Assigning purchases to guilds

A user purchase can be moved between guilds:

- `enableSubscription(purchase, guild)` adds the subscription to the guild and assigns the purchase. If it was
  assigned to another guild, it is removed there first.
- `disableSubscription(purchase)` removes the subscription from its guild and unassigns the purchase.

`enableSubscription` returns a `SubscriptionResult`:

| Result | Meaning |
|---|---|
| `SUCCESS` | The subscription is active in the guild. |
| `ALREADY_SUBSCRIBED` | The guild has this subscription already. |
| `LOCKED` | A guild purchase can not move to another guild. |
| `SUBSCRIPTION_EXPIRED` | The purchase has expired. |
| `UNKOWN` | `assignToGuild` returned `false`. |

`refresh(guild)` loads the active entitlements of a guild from Discord, removes the guild's Discord subscriptions,
registers the purchases again and enables them for the guild. User purchases that were assigned to the guild are not
part of that list; enable them again with `enableSubscription`.

A purchase from an entitlement that never ends has no expiry (`null`). The subscription a purchase adds to a guild
carries the entitlement type of the purchase.
