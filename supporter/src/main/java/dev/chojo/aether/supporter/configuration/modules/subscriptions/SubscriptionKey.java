package dev.chojo.aether.supporter.configuration.modules.subscriptions;

import dev.chojo.aether.supporter.registry.Key;

/// Represents a unique identifier for a subscription.
/// Each subscription needs a unique identifier.
public record SubscriptionKey(String name) implements Key {
}
