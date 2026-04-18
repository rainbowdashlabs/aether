/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.supporter.configuration.modules.subscriptions.platform;

import dev.chojo.aether.supporter.configuration.modules.subscriptions.platform.purchase.PurchaseType;

import java.util.Collections;
import java.util.Map;

/// Represents a plaform configuration for a subscription on a platform
/// A default identifier can be found at [PurchaseType]
@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
public class PlatformSubscription {

    private Map<PurchaseType, String> ids;

    /// A map containing the platform ids for the purchase
    ///
    /// @return An unmodifiable view of the platform ids
    public Map<PurchaseType, String> ids() {
        return Collections.unmodifiableMap(ids);
    }

    /// Get the id of a purchase type
    ///
    /// @param type the purchase type to get the id for
    /// @return id of the purchase type
    public String subscriptionId(PurchaseType type) {
        return ids.get(type);
    }

    /// Check if a given subscription id is a lifetime subscription on this platform
    ///
    /// @param subscriptionId the subscription id to check
    /// @return true if the subscription id is a lifetime subscription, false otherwise
    public boolean isLifetime(String subscriptionId) {
        for (var entry : ids.entrySet()) {
            if (entry.getValue().equals(subscriptionId)) return entry.getKey().isLifetime();
        }
        return false;
    }
}
