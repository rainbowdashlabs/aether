/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.supporter.registry;

import dev.chojo.aether.supporter.configuration.modules.subscriptions.SubscriptionKey;
import dev.chojo.aether.supporter.configuration.modules.subscriptions.platform.Platform;
import dev.chojo.aether.supporter.configuration.modules.subscriptions.platform.purchase.PurchaseType;

/**
 * Central registry for all supporter related keys.
 */
public final class SupporterRegistry {
    /**
     * Registry for all supported platforms.
     */
    public static final Registry<Platform> PLATFORMS = new Registry<>(Platform.class);
    /**
     * Registry for all subscription types.
     */
    public static final Registry<SubscriptionKey> SUBSCRIPTION_TYPES = new Registry<>(SubscriptionKey.class);
    /**
     * Registry for all purchase types.
     */
    public static final Registry<PurchaseType> PURCHASE_TYPE = new Registry<>(PurchaseType.class);
}
