/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.supporter.access;

import dev.chojo.aether.supporter.configuration.modules.subscriptions.platform.Platform;

import java.util.List;

/**
 * Access to the subscriptions of an entity
 */
public interface Subscriptions {
    /**
     * Subscriptions of the entity
     */
    List<Subscription> subscriptions();

    default boolean hasAccess(long subscriptionId) {
        return subscriptions().stream().anyMatch(sub -> sub.subscriptionId() == subscriptionId);
    }

    /**
     * Delete a subscription from the entity
     *
     * @param subscription Subscription to delete
     */
    void deleteSubscription(Subscription subscription);

    /**
     * Add a subscription to the entity
     *
     * @param subscription Subscription to add
     * @return true if the subscription was added, false if it already exists
     */
    boolean addSubscription(Subscription subscription);

    /**
     * Clear all subscriptions of a platform of this entity
     *
     * @param source platform
     */
    void clear(Platform source);
}
