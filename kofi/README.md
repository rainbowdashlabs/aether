# Ko-fi Module

[![Maven Central](https://img.shields.io/maven-central/v/dev.chojo.aether/kofi)](https://central.sonatype.com/artifact/dev.chojo.aether/kofi)

The Ko-fi module provides integration with [Ko-fi](https://ko-fi.com) via webhooks. It allows handling donations, subscriptions, and shop orders, mapping them to internal subscriptions for users and guilds.

## Installation

### Gradle (Kotlin DSL)

```kotlin
implementation("dev.chojo.aether:kofi:version")
```

### Maven

```xml
<dependency>
    <groupId>dev.chojo.aether</groupId>
    <artifactId>kofi</artifactId>
    <version>version</version>
</dependency>
```

## Features

- **Webhook Handling**: Receive and validate Ko-fi webhooks using a verification token.
- **Auto-Registration**: Automatically links Ko-fi purchases to Discord users if a Discord ID is provided in the transaction.
- **Subscription Management**: Supports both monthly subscriptions and lifetime purchases (shop orders).
- **Guild Assignment**: Allows users to assign their Ko-fi purchases to a specific Discord guild to unlock features.
- **Expiration Tracking**: Automatically handles the expiration of Ko-fi subscriptions.

## Setup

To use the Ko-fi service, you need to extend the `KofiService` class and provide the necessary configurations.

### Configuration

The `Kofi` configuration class requires your Ko-fi verification token:

```java
Kofi kofiConfig = new Kofi();
// Set the token via reflection or your configuration loader
// kofiConfig.token("your_verification_token");
```

### Implementation

You need to implement the abstract methods of `KofiService` to handle persistence and guild-specific logic:

```java
public class MyKofiService extends KofiService {
    public MyKofiService(Kofi configuration, 
                        UserProvider userProvider, 
                        MailService mailService, 
                        SupporterConfiguration<?, ?, ?> supporterConfiguration) {
        super(configuration, userProvider, mailService, supporterConfiguration);
    }

    @Override
    protected void registerPurchase(KofiPurchase purchase) {
        // Save the purchase to your database
    }

    @Override
    protected List<KofiPurchase> expiredPurchases() {
        // Retrieve expired purchases from your database
        return myDatabase.getExpiredKofiPurchases();
    }

    @Override
    protected Subscriptions guildSubscriptions(long guildId) {
        // Return the Subscriptions object for the given guild
        return myRegistry.getGuildSubscriptions(guildId);
    }

    @Override
    public KofiPurchase buildPurchase(String mailHash, String transactionId, String key, Type type, long subscriptionId, Instant expiresAt) {
        // Return your implementation of KofiPurchase
        return new MyKofiPurchase(mailHash, transactionId, key, type, subscriptionId, expiresAt);
    }
}
```

## How it works

### Webhook Processing

When a webhook is received from Ko-fi, it should be passed to `KofiService#handle(KofiTransaction)`.

1.  **Validation**: The service checks if the `verification_token` matches the configured token.
2.  **User Mapping**: 
    - It hashes the sender's email.
    - If a Discord User ID is present in the transaction, it automatically registers the email as a verified mail for that user in the `MailService`.
    - If no user is found, it sends a notification email to the purchaser.
3.  **Purchase Creation**: If the transaction is a `SUBSCRIPTION` or `SHOP_ORDER`, it creates one or more `KofiPurchase` objects and calls `registerPurchase`.

### Subscriptions and Shop Orders

- **Subscriptions**: Mapped using the `tier_name`. The service sets an expiration date 32 days into the future by default.
- **Shop Orders**: Mapped using the `direct_link_code` of the shop items. These are treated as lifetime purchases.

### Guild Assignment

Users can assign a purchase to a guild using `enableSubscription(KofiPurchase, Guild)`. This will:
1.  Check if the purchase is still valid.
2.  Add a new `Subscription` to the guild's subscription list.
3.  Associate the purchase with the guild ID in your persistence layer.

### Expiration

The service runs a scheduled task (every 60 minutes) that calls `removeExpiredSubs()`. This method retrieves expired purchases via `expiredPurchases()`, disables them from their respective guilds, and deletes them.
