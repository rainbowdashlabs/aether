/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.supporter.service.context;

import dev.chojo.aether.supporter.configuration.SupporterConfiguration;
import dev.chojo.aether.supporter.configuration.modules.subscriptions.platform.purchase.Purchase;

public class SupporterValidator<
        SubscriptionID extends Enum<?>,
        FeatureID extends Enum<?>,
        Platform extends Enum<?>,
        PurchaseType extends Enum<PurchaseType> & Purchase,
        Price,
        FeatureMeta> {
    private final SubscriptionContext subscriptionContext;
    private final SupporterConfiguration<SubscriptionID, FeatureID, Platform, PurchaseType, Price, FeatureMeta>
            configuration;

    public SupporterValidator(
            SubscriptionContext subscriptionContext,
            SupporterConfiguration<SubscriptionID, FeatureID, Platform, PurchaseType, Price, FeatureMeta>
                    configuration) {
        this.subscriptionContext = subscriptionContext;
        this.configuration = configuration;
    }

    public boolean hasAccess(FeatureID feature) {
        return subscriptionContext.hasAccess(configuration.feature(feature));
    }
}
