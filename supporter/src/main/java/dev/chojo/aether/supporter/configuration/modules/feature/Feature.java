/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.supporter.configuration.modules.feature;

import dev.chojo.aether.supporter.service.context.AccessCheckResult;
import dev.chojo.aether.supporter.service.context.SubscriptionContext;

import java.util.Set;

/**
 * Represents a feature that can be accessed by users with a subscription.
 * This class may be extended to define features and define the generic arguments.
 * If no price and or meta should be set, the {@link dev.chojo.aether.supporter.configuration.modules.dummy.Empty} class can be used.
 *
 * @param <Meta>  Additional metadata associated with the feature
 * @param <Price> Price information for a feature.
 */
@SuppressWarnings("FieldMayBeFinal")
public abstract class Feature<Price, Meta> {
    /**
     * General unique id for the feature
     * This id should be unique across all features, and subscriptions should not be changed once set.
     */
    private long id;

    /**
     * Human-readable name
     */
    private String name;

    /**
     * Price information for a feature.
     */
    private Price price;

    /**
     * Additional metadata associated with the feature
     */
    private Meta meta;

    /**
     * List of ids which show that an entity is eligible for accessing this feature. This id might also contain the
     * {@link dev.chojo.aether.supporter.configuration.modules.subscriptions.Subscription#id()}
     */
    private Set<Long> enabledBy;

    /**
     * Creates a new feature with the given parameters
     *
     * @param id        General unique id for the feature. This id should be unique across all features, and
     *                  subscriptions. Should not be changed once set.
     * @param name      Human-readable name for the feature
     * @param price     Price information for the feature
     * @param meta      Additional metadata associated with the feature
     * @param enabledBy List of ids which show that an entity is eligible for accessing this feature. This id might
     *                  also contain the {@link dev.chojo.aether.supporter.configuration.modules.subscriptions.Subscription#id()}
     */
    public Feature(int id, String name, Price price, Meta meta, Set<Long> enabledBy) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.meta = meta;
        this.enabledBy = enabledBy;
    }

    /**
     * Creates a new feature with the given parameters
     * Will infere the name from the class name
     *
     * @param id        General unique id for the feature. This id should be unique across all features, and
     *                  subscriptions. Should not be changed once set.
     * @param price     Price information for the feature
     * @param meta      Additional metadata associated with the feature
     * @param enabledBy List of ids which show that an entity is eligible for accessing this feature. This id might
     *                  also contain the {@link dev.chojo.aether.supporter.configuration.modules.subscriptions.Subscription#id()}
     */
    public Feature(int id, Price price, Meta meta, Set<Long> enabledBy) {
        this.id = id;
        this.name = getClass().getSimpleName();
        this.price = price;
        this.meta = meta;
        this.enabledBy = enabledBy;
    }

    public AccessCheckResult test(SubscriptionContext subscriptionContext) {
        if (enabledBy.isEmpty()) return AccessCheckResult.hasAccess(true);
        if (subscriptionContext.enabledSubscriptions().contains(id)) return AccessCheckResult.hasAccess(true);
        return AccessCheckResult.hasAccess(
                enabledBy.stream().anyMatch(subscriptionContext.enabledSubscriptions()::contains));
    }
}
