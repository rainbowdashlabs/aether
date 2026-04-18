/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.kofi.pojo;


import java.time.Instant;
import java.time.temporal.ChronoUnit;

/// Represents a purchase made on kofi. This maybe be a subscription or a lifetime purchase.
/// The Mail hash might have a matching [dev.chojo.aether.mailing.entities.MailEntry]
public abstract class KofiPurchase {
    private final long id;
    /// A hash of the user mail.
    private final String mailHash;

    private final String transactionId;
    /// Key of the product
    /// For purchases, it's the shop shortcode.
    /// For subscriptions, it's the tier name.
    private final String key;
    /// The type of purchase.
    private final Type type;

    /// The id associated with this purchase.
    /// This id has to match the id of a [dev.chojo.aether.supporter.configuration.modules.subscriptions.Subscription]
    private final long subscriptionId;

    /// The date where this purchase expires.
    /// Only applicable if {@link #type} is {@link Type#SUBSCRIPTION}.
    private final Instant expiresAt;

    /// The guild on which this purchase is active.
    private final long guildId;

    /// Creates a new [KofiPurchase] that is not associated to a guild
    ///
    /// @param mailHash      mail hash of the purchase
    /// @param transactionId transaction id of the purchase
    /// @param key           Key of the product. For purchases, it's the shop shortcode. For subscriptions, it's the
    // tier name.
    public KofiPurchase(
            String mailHash, String transactionId, String key, Type type, long subscriptionId, Instant expiresAt) {
        this(-1, mailHash, transactionId, key, type, subscriptionId, expiresAt, 0);
    }

    public KofiPurchase(
            long id,
            String mailHash,
            String transactionId,
            String key,
            Type type,
            long subscriptionId,
            Instant expiresAt,
            long guildId) {
        this.id = id;
        this.mailHash = mailHash;
        this.transactionId = transactionId;
        this.guildId = guildId;
        this.key = key;
        this.type = type;
        this.subscriptionId = subscriptionId;
        this.expiresAt = expiresAt;
    }

    public long id() {
        return id;
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

    /// The is of the subscription associated with this purchase
    /// The id has to match a [dev.chojo.aether.supporter.configuration.modules.subscriptions.Subscription]
    public long subscriptionId() {
        return subscriptionId;
    }

    /// The time the purchase expires
    public Instant expiresAt() {
        return expiresAt;
    }

    /// Guild id associated with this purchase
    /// @return guild id or 0 if not assigned
    public long guildId() {
        return guildId;
    }

    /// Assign a kofi purchase to a guild
    /// This associates the purchase with a guild id
    public abstract boolean assignPurchaseToGuild(long guildId);

    /// Unassign a kofi purchase from a guild
    /// This keeps the purchase but reverts the associated guild id to 0
    public abstract boolean unassignPurchaseFromGuild();

    /// Renew a kofi purchase that represents a subscription
    /// Sets the expiration date to 32 days from now
    public abstract void renew();

    /// Delete the kofi purchase
    public abstract void delete();

    public boolean isValid() {
        return expiresAt.isAfter(Instant.now().minus(32, ChronoUnit.DAYS));
    }
}
