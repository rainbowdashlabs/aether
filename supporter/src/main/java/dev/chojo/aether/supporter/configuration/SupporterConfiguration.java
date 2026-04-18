/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.supporter.configuration;

import dev.chojo.aether.supporter.configuration.modules.Interactions;
import dev.chojo.aether.supporter.configuration.modules.feature.Feature;
import dev.chojo.aether.supporter.configuration.modules.subscriptions.Subscription;
import dev.chojo.aether.supporter.configuration.modules.subscriptions.SubscriptionKey;
import dev.chojo.aether.supporter.configuration.modules.subscriptions.platform.Platform;
import dev.chojo.aether.supporter.configuration.modules.subscriptions.platform.purchase.PurchaseType;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
public class SupporterConfiguration<
        FeatureID extends Enum<?>,
        Price,
        FeatureMeta> {
    private Map<SubscriptionKey, Subscription> subscriptions;
    private Map<FeatureID, Feature<Price, FeatureMeta>> features;
    private Interactions interactions;

    public Feature<Price, FeatureMeta> feature(FeatureID featureID) {
        return features.get(featureID);
    }

    public Subscription subscription(SubscriptionKey subscriptionID) {
        return subscriptions.get(subscriptionID);
    }

    public Collection<Subscription> subscriptions() {
        return Collections.unmodifiableCollection(subscriptions.values());
    }

    public Optional<Subscription> byId(long id) {
        return subscriptions().stream().filter(sub -> sub.id() == id).findFirst();
    }

    public Optional<Subscription> findSubscription(Platform platform, PurchaseType type, String id) {
        return subscriptions().stream()
                              .filter(sub -> sub.platformSubscription(platform)
                                                .subscriptionId(type)
                                                .equals(id))
                              .findFirst();
    }

    public Interactions interactions() {
        return interactions;
    }
}
