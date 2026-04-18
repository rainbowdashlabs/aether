/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.supporter.registry;

import dev.chojo.aether.supporter.configuration.modules.subscriptions.SubscriptionKey;
import dev.chojo.aether.supporter.configuration.modules.subscriptions.platform.Platform;
import dev.chojo.aether.supporter.configuration.modules.subscriptions.platform.purchase.PurchaseType;

public final class SupporterRegistry {
    public static final Registry<Platform> PLATFORMS = new Registry<>(Platform.class);
    public static final Registry<SubscriptionKey> SUBSCRIPTION_TYPES = new Registry<>(SubscriptionKey.class);
    public static final Registry<PurchaseType> PURCHASE_TYPE = new Registry<>(PurchaseType.class);
}
