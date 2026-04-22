/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.supporter.registry;

import dev.chojo.aether.common.registry.Registry;
import dev.chojo.aether.supporter.configuration.modules.subscriptions.SubscriptionKey;
import dev.chojo.aether.supporter.configuration.modules.subscriptions.platform.Platform;
import dev.chojo.aether.supporter.configuration.modules.subscriptions.platform.purchase.PurchaseType;

import static dev.chojo.aether.supporter.configuration.modules.subscriptions.platform.Platform.DISCORD;
import static dev.chojo.aether.supporter.configuration.modules.subscriptions.platform.Platform.KOFI;
import static dev.chojo.aether.supporter.configuration.modules.subscriptions.platform.Platform.PATREON;
import static dev.chojo.aether.supporter.configuration.modules.subscriptions.platform.purchase.PurchaseType.LIFETIME;
import static dev.chojo.aether.supporter.configuration.modules.subscriptions.platform.purchase.PurchaseType.SUBSCRIPTION;

/**
 * Central registry for all supporter-related keys.
 */
public final class SupporterRegistry {
    /**
     * Registry for all supported platforms.
     */
    public static final Registry<Platform> PLATFORMS =
            new Registry<>(Platform.class).register(DISCORD).register(KOFI).register(PATREON);

    /**
     * Registry for all subscription types.
     */
    public static final Registry<SubscriptionKey> SUBSCRIPTION_TYPES = new Registry<>(SubscriptionKey.class);

    /**
     * Registry for all purchase types.
     */
    public static final Registry<PurchaseType> PURCHASE_TYPE =
            new Registry<>(PurchaseType.class).register(SUBSCRIPTION).register(LIFETIME);
}
