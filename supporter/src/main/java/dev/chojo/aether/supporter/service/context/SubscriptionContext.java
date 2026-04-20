/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.supporter.service.context;

import dev.chojo.aether.supporter.configuration.modules.feature.Feature;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents the set of enabled subscriptions for a user or guild.
 */
public interface SubscriptionContext {
    /**
     * Returns the set of enabled subscription IDs.
     * @return The set of enabled subscription IDs.
     */
    Set<Long> enabledSubscriptions();

    /**
     * Merges this context with another one.
     *
     * @param other The context to merge with.
     * @return A new context containing the merged subscriptions.
     */
    default SubscriptionContext merge(SubscriptionContext other) {
        Set<Long> mergedSubscriptions = new HashSet<>(Set.copyOf(enabledSubscriptions()));
        mergedSubscriptions.addAll(other.enabledSubscriptions());
        return () -> mergedSubscriptions;
    }

    /**
     * Checks if the given feature is accessible with the current subscriptions.
     *
     * @param feature The feature.
     * @return The result of the access check.
     */
    default AccessCheckResult hasAccess(Feature<?, ?> feature) {
        return feature.test(this);
    }

    /**
     * Returns whether the context has no enabled subscriptions.
     * @return true if no subscriptions are enabled.
     */
    default boolean isEmpty() {
        return enabledSubscriptions().isEmpty();
    }
}
