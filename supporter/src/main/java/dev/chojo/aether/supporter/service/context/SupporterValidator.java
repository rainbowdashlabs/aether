/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.supporter.service.context;

import dev.chojo.aether.supporter.configuration.SupporterConfiguration;

public class SupporterValidator<FeatureID extends Enum<?>, Price, FeatureMeta> {
    private final SubscriptionContext subscriptionContext;
    private final SupporterConfiguration<FeatureID, Price, FeatureMeta> configuration;

    public SupporterValidator(
            SubscriptionContext subscriptionContext,
            SupporterConfiguration<FeatureID, Price, FeatureMeta> configuration) {
        this.subscriptionContext = subscriptionContext;
        this.configuration = configuration;
    }

    public AccessCheckResult hasAccess(FeatureID feature) {
        return subscriptionContext.hasAccess(configuration.feature(feature));
    }
}
