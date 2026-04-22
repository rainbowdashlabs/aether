/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.kofi.pojo;

import dev.chojo.aether.mailing.entities.AMailEntry;

import java.time.Instant;

/**
 * Represents a purchase made on kofi. This maybe be a subscription or a lifetime purchase.
 * The Mail hash might have a matching {@link AMailEntry#hash()}
 */
public abstract class AKofiPurchase {
    /**
     * A hash of the user mail.
     * @see AMailEntry#hash()
     */
    private final String mailHash;

    /**
     * Transaction id of the purchase.
     * If the purchase it is a {@link Type#SHOP_ORDER}, there will be a number appended, so each item is unique
     * @see AKofiPurchase#transactionId()
     */
    private final String transactionId;
    /**
     * Key of the product
     * For purchases, it's the shop shortcode.
     * For subscriptions, it's the tier name.
     * @see AKofiPurchase#key()
     */
    private final String key;

    /**
     * The type of purchase.
     */
    private final Type type;

    /**
     * The id associated with this purchase.
     * @see dev.chojo.aether.supporter.configuration.modules.subscriptions.Subscription#id()
     */
    private final long subscriptionId;

    /**
     * The date where this purchase expires.
     * Only applicable if {@link #type} is {@link Type#SUBSCRIPTION}.
     */
    private final Instant expiresAt;

    /**
     * The guild on which this purchase is active.
     */
    private final long guildId;

    /**
     * Creates a new {@link AKofiPurchase} that is not associated to a guild
     *
     * @param mailHash      mail hash of the purchase
     * @param transactionId transaction id of the purchase
     * @param key           Key of the product. For purchases, it's the shop shortcode. For subscriptions, it's the
     *                      tier name.
     */
    public AKofiPurchase(
            String mailHash, String transactionId, String key, Type type, long subscriptionId, Instant expiresAt) {
        this(mailHash, transactionId, key, type, subscriptionId, expiresAt, 0);
    }

    public AKofiPurchase(
            String mailHash,
            String transactionId,
            String key,
            Type type,
            long subscriptionId,
            Instant expiresAt,
            long guildId) {
        this.mailHash = mailHash;
        this.transactionId = transactionId;
        this.guildId = guildId;
        this.key = key;
        this.type = type;
        this.subscriptionId = subscriptionId;
        this.expiresAt = expiresAt;
    }

    public String mailHash() {
        return mailHash;
    }

    public String transactionId() {
        return transactionId;
    }

    public String key() {
        return key;
    }

    public Type type() {
        return type;
    }

    /**
     * The is of the subscription associated with this purchase
     * The id has to match a {@link dev.chojo.aether.supporter.configuration.modules.subscriptions.Subscription}
     */
    public long subscriptionId() {
        return subscriptionId;
    }

    /**
     * The time the purchase expires
     */
    public Instant expiresAt() {
        return expiresAt;
    }

    /**
     * Guild id associated with this purchase
     * @return guild id or 0 if not assigned
     */
    public long guildId() {
        return guildId;
    }

    /**
     * Assign a kofi purchase to a guild
     * This associates the purchase with a guild id
     */
    public abstract boolean assignToGuild(long guildId);

    /**
     * Unassign a kofi purchase from a guild
     * This keeps the purchase but reverts the associated guild id to 0
     */
    public abstract boolean unassignFromGuild();

    /**
     * Renew a kofi purchase that represents a subscription
     * Sets the expiration date to 32 days from now
     */
    public abstract void renew();

    /**
     * Delete the kofi purchase
     */
    public abstract void delete();

    /**
     * Check if the purchase is still valid.
     * @return true if the purchase is still valid
     */
    public boolean isValid() {
        return expiresAt.isAfter(Instant.now());
    }
}
