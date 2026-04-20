/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.supporter.service.context;

import dev.chojo.aether.supporter.configuration.SupporterConfiguration;

/**
 * A validator for checking access to features.
 * This class is typically injected into the command's key-value store.
 *
 * @param <FeatureID>   The enum type representing the features.
 */
public class SupporterValidator<FeatureID extends Enum<?>> {
    private final SubscriptionContext subscriptionContext;
    private final SupporterConfiguration<FeatureID, ?, ?> configuration;

    public SupporterValidator(
            SubscriptionContext subscriptionContext, SupporterConfiguration<FeatureID, ?, ?> configuration) {
        this.subscriptionContext = subscriptionContext;
        this.configuration = configuration;
    }

    /**
     * Checks if the given feature is accessible.
     *
     * @param feature The feature.
     * @return The result of the access check.
     */
    public AccessCheckResult hasAccess(FeatureID feature) {
        return subscriptionContext.hasAccess(configuration.feature(feature));
    }
}
