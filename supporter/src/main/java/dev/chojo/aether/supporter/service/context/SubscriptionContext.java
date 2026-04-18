/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.supporter.service.context;

import dev.chojo.aether.supporter.configuration.modules.feature.Feature;

import java.util.HashSet;
import java.util.Set;

public interface SubscriptionContext {
    Set<Long> enabledSubscriptions();

    /// Merges two contexts
    ///
    /// @param other The context to merge with
    /// @return A new context with merged subscriptions
    default SubscriptionContext merge(SubscriptionContext other) {
        Set<Long> mergedSubscriptions = new HashSet<>(Set.copyOf(enabledSubscriptions()));
        mergedSubscriptions.addAll(other.enabledSubscriptions());
        return () -> mergedSubscriptions;
    }

    default AccessCheckResult hasAccess(Feature<?, ?> feature) {
        return feature.test(this);
    }

    default AccessCheckResult hasAccess(Set<Long> enabledBy) {
        if (enabledBy.isEmpty()) return AccessCheckResult.success();
        return new AccessCheckResult(enabledBy, enabledBy.stream().anyMatch(enabledSubscriptions()::contains));
    }

    default boolean isEmpty() {
        return enabledSubscriptions().isEmpty();
    }
}
