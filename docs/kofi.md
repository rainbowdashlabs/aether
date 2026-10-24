---
title: Ko-fi
sidebar_position: 4
---

# Ko-fi

The `kofi` module turns [Ko-fi](https://ko-fi.com) webhooks into purchases and guild subscriptions of the
[supporter](./supporter/index.md) module. It uses [mailing](./mailing.md) to link the buyer's mail address to a
Discord user.

```kotlin
implementation("dev.chojo.aether:kofi:2.0.0")
```

## Configuration

`Kofi` has one key:

| Key | Meaning |
|---|---|
| `token` | The verification token from your Ko-fi webhook settings. Without it every webhook is rejected. |

Ko-fi tiers and shop items are matched through the `KOFI` platform ids of your
[supporter configuration](./supporter/configuration.md#subscriptions): the tier name for `SUBSCRIPTION`, the
shop item's direct link code for `LIFETIME`.

## Your purchase class

Extend `AKofiPurchase`. It holds the mail hash of the buyer, the transaction id, the key (tier name or direct link
code), the Ko-fi `Type`, the subscription id, the expiry and the guild (`0` for none). You implement
`assignToGuild(guildId)`, `unassignFromGuild()`, `renew()` and `delete()`.

## Your service

Extend `AKofiService`:

```java
public class KofiService extends AKofiService<KofiPurchase> {
    private final PurchaseRepository repository;

    public KofiService(Kofi configuration, IUserProvider users, AMailService<?> mailService,
            SupporterConfiguration<?, ?, ?> supporterConfiguration, PurchaseRepository repository) {
        super(configuration, users, mailService, supporterConfiguration);
        this.repository = repository;
    }

    @Override
    protected void registerPurchase(KofiPurchase purchase) {
        repository.insert(purchase);
    }

    @Override
    protected List<KofiPurchase> expiredPurchases() {
        return repository.expiredKofiPurchases();
    }

    @Override
    protected ISubscriptions guildSubscriptions(long guildId) {
        return repository.guildSubscriptions(guildId);
    }

    @Override
    protected KofiPurchase buildPurchase(String mailHash, String transactionId, String key, Type type,
            long subscriptionId, Instant expiresAt) {
        return new KofiPurchase(mailHash, transactionId, key, type, subscriptionId, expiresAt);
    }
}

KofiService kofiService = new KofiService(kofi, users, mailService, supporterConfiguration, repository);
kofiService.start();
```

`start()` schedules the cleanup of expired purchases: every 60 minutes, starting 40 minutes later, each purchase from
`expiredPurchases()` is removed from its guild and deleted. The service does not call `start()` itself.

## Receiving the webhook

Ko-fi sends a form with a single field `data` that holds JSON. Read it into a `KofiTransaction` and pass it to
`handle`. With Javalin:

```java
JsonMapper mapper = JsonMapper.builder().build();

app.post("/webhook/kofi", ctx -> {
    KofiTransaction transaction = mapper.readValue(ctx.formParam("data"), KofiTransaction.class);
    try {
        kofiService.handle(transaction);
        ctx.status(HttpStatus.OK);
    } catch (UnauthorizedException e) {
        ctx.status(HttpStatus.UNAUTHORIZED);
    }
});
```

`handle` does this:

1. It throws `UnauthorizedException` when no `token` is configured or the verification token does not match it.
2. If the buyer's mail address is not known yet:
   - with a Discord user id in the transaction, the address is registered as verified for that user,
   - without one, the buyer gets the `link_account.html` mail, which asks them to link their account
     (see [Mailing](./mailing.md#templates)).
3. For a subscription payment it creates one purchase that expires in 32 days.
4. For a shop order it creates one purchase per item and quantity, without expiry. Their transaction ids get `-0`,
   `-1` and so on appended.
5. Each purchase goes to `registerPurchase`. Donations and commissions create no purchase.

Purchases are not assigned to a guild yet. Let the user pick one, then call `enableSubscription(purchase, guild)`.
It returns a `SubscriptionResult` like the [Discord service](./supporter/discord-entitlements.md#assigning-purchases-to-guilds).
`disableSubscription(purchase)` removes it from its guild again.

A tier name or direct link code that is not in your configuration is logged as an error and creates no purchase.
Shop order purchases never expire; `isValid()` is always `true` for them.
