/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.supporter.configuration.modules.subscriptions;

import dev.chojo.aether.supporter.configuration.modules.subscriptions.platform.Platform;
import dev.chojo.aether.supporter.configuration.modules.subscriptions.platform.PlatformSubscription;

import java.util.HashMap;
import java.util.Map;

/// Represents a subscription configuration
/// A subscription might be retrieved on different platforms
/// A default platform set can be found in [Platform]
@SuppressWarnings({"FieldMayBeFinal", "MismatchedQueryAndUpdateOfCollection"})
public class Subscription {
    /// Human-readable name of the subscription
    private String name;

    /// Unique id for this subscription
    /// This id should be unique across all features, and subscriptions should not be changed once set.
    private int id;

    /// Map holding subscription information for each platform
    private Map<Platform, PlatformSubscription> platforms = new HashMap<>();

    /// Human-readable name of the subscription
    public String name() {
        return name;
    }

    /// Subscription info for the platform
    public PlatformSubscription platformSubscription(Platform platform) {
        return platforms.get(platform);
    }

    public int id() {
        return id;
    }
}
