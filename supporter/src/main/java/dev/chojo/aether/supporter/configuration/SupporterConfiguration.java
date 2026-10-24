/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.supporter.configuration;

import dev.chojo.aether.supporter.configuration.modules.Interactions;
import dev.chojo.aether.supporter.configuration.modules.feature.AFeature;
import dev.chojo.aether.supporter.configuration.modules.subscriptions.Subscription;
import dev.chojo.aether.supporter.configuration.modules.subscriptions.SubscriptionKey;
import dev.chojo.aether.supporter.configuration.modules.subscriptions.platform.Platform;
import dev.chojo.aether.supporter.configuration.modules.subscriptions.platform.PlatformSubscription;
import dev.chojo.aether.supporter.configuration.modules.subscriptions.platform.purchase.PurchaseType;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Configuration for the supporter module.
 * This class holds the definition of subscriptions, features and interaction based access rules.
 *
 * @param <FeatureID>   The enum type representing the features.
 * @param <Price>       The type representing the price of a feature.
 * @param <FeatureMeta> The type representing additional metadata for a feature.
 */
@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
public class SupporterConfiguration<FeatureID extends Enum<?>, Price, FeatureMeta> {
    private Map<SubscriptionKey, Subscription> subscriptions;
    private Map<FeatureID, AFeature<Price, FeatureMeta>> features;
    private Interactions interactions;

    /**
     * Returns the feature with the given id.
     *
     * @param featureID The id of the feature.
     * @return The feature.
     */
    public AFeature<Price, FeatureMeta> feature(FeatureID featureID) {
        return features.get(featureID);
    }

    /**
     * Returns the subscription with the given id.
     *
     * @param subscriptionID The id of the subscription.
     * @return The subscription.
     */
    public Subscription subscription(SubscriptionKey subscriptionID) {
        return subscriptions.get(subscriptionID);
    }

    /**
     * Finds the purchase type a platform specific id stands for. Subscriptions without the platform are skipped.
     *
     * @param platform The platform.
     * @param sku      The platform specific id.
     * @return An optional containing the purchase type if found.
     */
    public Optional<PurchaseType> findPurchaseTypeBySkuID(Platform platform, String sku) {
        return platformSubscriptions(platform)
                .map(p -> p.subscriptionType(sku))
                .flatMap(Optional::stream)
                .findFirst();
    }

    /**
     * Finds the subscription that lists a platform specific id for any purchase type. Subscriptions without the platform
     * are skipped.
     *
     * @param platform The platform.
     * @param sku      The platform specific id.
     * @return An optional containing the subscription if found.
     */
    public Optional<Subscription> findSubscriptionBySkuID(Platform platform, String sku) {
        return subscriptions().stream()
                .filter(sub -> sub.platformSubscription(platform) != null)
                .filter(sub ->
                        sub.platformSubscription(platform).subscriptionType(sku).isPresent())
                .findFirst();
    }

    private Stream<PlatformSubscription> platformSubscriptions(Platform platform) {
        return subscriptions().stream()
                .map(sub -> sub.platformSubscription(platform))
                .filter(Objects::nonNull);
    }

    /**
     * Returns all registered subscriptions.
     *
     * @return A collection of all subscriptions.
     */
    public Collection<Subscription> subscriptions() {
        return Collections.unmodifiableCollection(subscriptions.values());
    }

    /**
     * Finds a subscription by its id.
     *
     * @param id The id of the subscription.
     * @return An optional containing the subscription if found.
     */
    public Optional<Subscription> byId(long id) {
        return subscriptions().stream().filter(sub -> sub.id() == id).findFirst();
    }

    /**
     * Finds a subscription by platform, type, and platform-specific id. Subscriptions without the platform or without an
     * id for the type are skipped.
     *
     * @param platform The platform.
     * @param type     The purchase type.
     * @param id       The platform specific id.
     * @return An optional containing the subscription if found.
     */
    public Optional<Subscription> findSubscription(Platform platform, PurchaseType type, String id) {
        return subscriptions().stream()
                .filter(sub -> sub.platformSubscription(platform) != null)
                .filter(sub -> Objects.equals(sub.platformSubscription(platform).subscriptionId(type), id))
                .findFirst();
    }

    /**
     * Returns the interaction based access rules.
     *
     * @return The interactions.
     */
    public Interactions interactions() {
        return interactions;
    }
}
