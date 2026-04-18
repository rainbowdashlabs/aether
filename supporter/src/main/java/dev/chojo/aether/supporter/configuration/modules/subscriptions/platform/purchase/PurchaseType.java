/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.supporter.configuration.modules.subscriptions.platform.purchase;

import dev.chojo.aether.supporter.registry.Key;

public record PurchaseType(String name, boolean isLifetime) implements Purchase, Key {
    public static final PurchaseType LIFETIME = new PurchaseType("LIFETIME", true);
    public static final PurchaseType SUBSCRIPTION = new PurchaseType("SUBSCRIPTION", false);

    @Override
    public boolean isLifetime() {
        return isLifetime;
    }
}
