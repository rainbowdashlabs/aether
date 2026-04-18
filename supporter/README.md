# Supporter Module

[![Maven Central](https://img.shields.io/maven-central/v/dev.chojo.aether/supporter)](https://central.sonatype.com/artifact/dev.chojo.aether/supporter)

The supporter module provides a flexible way to manage and validate user and guild subscriptions, features, and command access. It integrates with JDAC (JDA Dispatcher and Context) to provide middleware-based access control for Discord interactions.

## Installation

### Gradle (Kotlin DSL)

```kotlin
implementation("dev.chojo.aether:supporter:1.0.0")
```

### Maven

```xml
<dependency>
    <groupId>dev.chojo.aether</groupId>
    <artifactId>supporter</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Supporter Middleware

The `SupporterMiddleware` is a JDAC middleware that handles subscription context building and access validation for Discord interactions.

### Setup

To use the `SupporterMiddleware`, you need to register it with your JDAC `Dispatcher`.

```java
SupporterMiddleware<FeatureID, Price, FeatureMeta> middleware = new SupporterMiddleware<>(
    contextProvider,
    configuration,
    errorSupplier
);

// Register with JDAC
builder.registerMiddleware(middleware);
```

### Components

The middleware requires three main components:

1.  **`SubcriptionContextProvider`**: Responsible for retrieving subscription information for users and guilds from your persistent storage.
2.  **`SupporterConfiguration`**: Holds the definition of subscriptions, features, and interaction-based access rules (usually loaded from a configuration file).
3.  **`SupporterErrorSupplier`**: Provides the messages to be sent to the user when they lack the required subscriptions to perform an action.

### Functionality

1.  **Context Building**: For every interaction, the middleware builds a `SubscriptionContext`. It merges:
    *   **Discord Entitlements**: Automatically detected from the interaction event (e.g., via Discord's built-in monetization).
    *   **User Subscriptions**: Retrieved via `SubcriptionContextProvider#getSubscriptionContext(User)`.
    *   **Guild Subscriptions**: Retrieved via `SubcriptionContextProvider#getSubscriptionContext(User, Guild)` (if the interaction is in a guild).
2.  **Interaction Access Check**: The middleware checks if the current interaction (Slash Command, User Context, or Message Context) is restricted in the `SupporterConfiguration`. If restrictions exist and the user/guild lacks the required subscription, the interaction is canceled.
3.  **Context Injection**: If access is granted, the `SubscriptionContext` and a `SupporterValidator` are injected into the `InvocationContext`'s key-value store, making them available to your command handlers.

## Interfaces

### `SubcriptionContextProvider`

This interface must be implemented to bridge the supporter module with your data source.

*   `getSubscriptionContext(User user)`: Returns the subscriptions active for the specific user.
*   `getSubscriptionContext(Guild guild)`: Returns the subscriptions active for the specific guild.
*   `getSubscriptionContext(User user, Guild guild)`: Returns a merged context for both user and guild (default implementation provided).

### `SubscriptionContext`

Represents the set of active subscriptions for a given scope.

*   `enabledSubscriptions()`: Returns a `Set<Long>` of active subscription IDs.
*   `hasAccess(Feature feature)`: Checks if any of the active subscriptions grant access to the specified feature.
*   `hasAccess(Set<Long> enabledBy)`: Checks if any of the active subscriptions are present in the `enabledBy` set.
*   `merge(SubscriptionContext other)`: Merges two contexts into a new one.

### `AccessCheckResult`

A record representing the result of an access check.

*   `hasAccess()`: Boolean indicating if access is granted.
*   `enabledBy()`: A `Set<Long>` of subscription IDs that would have granted access (useful for error messages).

### `SupporterErrorSupplier`

Used to customize the feedback given to users when access is denied.

*   `getCommandError(AccessCheckResult result)`: Returns a `MessageCreateData` for command interactions.
*   `getAutoCompleteError()`: Returns a String (or error indicator) for auto-complete interactions.

## Configuration

The `SupporterConfiguration` class is the central point for defining your subscriptions and features. It is typically loaded from a JSON or YAML file.

### Structure

The configuration consists of three main parts:

1.  **`subscriptions`**: A map of `SubscriptionKey` to `Subscription`. Defines available subscription tiers.
2.  **`features`**: A map of `FeatureID` to `Feature`. Defines what features exist and which subscriptions grant access to them.
3.  **`interactions`**: Defines access restrictions for Discord commands and interactions.

## Registries

The module uses a registry system to allow for extensible keys like platforms, subscription types, and purchase types. These are managed via `SupporterRegistry`.

**Important:** Entries to the registry MUST be added BEFORE loading the configuration or registering the `SupporterModule` with Jackson, as the deserialization process relies on these registries to resolve keys.

Before loading a configuration or using the module, you should register any custom keys if they are not already present.

### Available Registries

*   `SupporterRegistry.PLATFORMS`: For registering `Platform` implementations (e.g., Discord, Ko-fi).
*   `SupporterRegistry.SUBSCRIPTION_TYPES`: For registering `SubscriptionKey` implementations.
*   `SupporterRegistry.PURCHASE_TYPE`: For registering `PurchaseType` implementations.

Example:
```java
SupporterRegistry.PLATFORMS.register(MyPlatform.INSTANCE);
```

## Jackson Serialization

The supporter module provides a Jackson `SupporterModule` to handle the serialization and deserialization of registry-based keys.

**Important:** For the configuration to be loaded correctly, you MUST register the `SupporterModule` with your `ObjectMapper`.

```java
ObjectMapper mapper = new ObjectMapper(); // or your preferred mapper setup
mapper.registerModule(new SupporterModule());
```

This ensures that `Platform`, `SubscriptionKey`, and `PurchaseType` instances are correctly resolved from their string names during deserialization by looking them up in their respective registries.

## Using the `SupporterValidator`

The `SupporterValidator` is injected into the command's key-value store and can be used within your command logic to perform fine-grained access checks.

```java
public void onSlashCommand(SlashCommandInteractionEvent event, InvocationContext<SlashCommandInteractionEvent> ctx) {
    SupporterValidator<FeatureID, Price, FeatureMeta> validator = ctx.keyValueStore().get(SupporterKeys.SUBSCRIPTION_VALIDATOR);
    
    AccessCheckResult result = validator.hasAccess(FeatureID.SOME_PREMIUM_FEATURE);
    if (!result.hasAccess()) {
        // Handle lack of access
        return;
    }
    
    // Proceed with premium logic
}
```

The keys used for injection are defined in `SupporterKeys`:
*   `SupporterKeys.SUBSCRIPTION_CONTEXT`: The raw `SubscriptionContext`.
*   `SupporterKeys.SUBSCRIPTION_VALIDATOR`: The `SupporterValidator` instance.
