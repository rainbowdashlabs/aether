/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.supporter.access;

import dev.chojo.aether.supporter.configuration.modules.subscriptions.platform.purchase.PurchaseType;
import net.dv8tion.jda.api.entities.Entitlement;
import net.dv8tion.jda.api.entities.Entitlement.EntitlementType;
import org.jspecify.annotations.Nullable;

import java.time.Instant;

public abstract class DiscordPurchase {
    /**
     * The user id of the user that owns this entitlement.
     *
     * @see Entitlement#getUserIdLong()
     */
    long userId;

    /**
     * The id of the sku that this entitlement is for.
     *
     * @see Entitlement#getSkuIdLong()
     */
    long skuId;

    /**
     * The type of entitlement.
     *
     * @see Entitlement#getType()
     */
    private EntitlementType type;

    /**
     * The target of the entitlement.
     *
     * @see SkuTarget#fromEntitlement(Entitlement)
     */
    SkuTarget target;

    /**
     * Id of the subscription that this entitlement is for.
     * Subscription id has to be associated with {@link dev.chojo.aether.supporter.configuration.modules.subscriptions.Subscription#id()}
     *
     * @see dev.chojo.aether.supporter.configuration.modules.subscriptions.Subscription#id()
     */
    long subscriptionId;

    /**
     * The id of the entitlement.
     *
     * @see Entitlement#getIdLong()
     */
    long entitlementId;

    /**
     * The time the entitlement expires.
     * If the instant is null, the entitlement does not expire.
     * An expiration date might not be set even if {@link #isPersistent()} returns {@code false}.
     */
    @Nullable
    private Instant expiresAt;

    /**
     * Set the persistence of an entitlement. This usally means that the {@link PurchaseType#isLifetime()} returns {@code true}
     */
    private boolean persistent;

    /**
     * The id of the guild that the purchase is attached to.
     * 0 if the purchase is not assigned to a guild.
     */
    private long guildId;

    public long userId() {
        return userId;
    }

    public long skuId() {
        return skuId;
    }

    public EntitlementType type() {
        return type;
    }

    public SkuTarget target() {
        return target;
    }

    public long subscriptionId() {
        return subscriptionId;
    }

    public long entitlementId() {
        return entitlementId;
    }

    public @Nullable Instant expiresAt() {
        return expiresAt;
    }

    public long guildId() {
        return guildId;
    }

    /**
     * Renew the entitlement.
     *
     * @param expiresAt the new expiration time
     */
    public abstract void renew(Instant expiresAt);

    /**
     * Delete the entitlement.
     */
    public abstract void delete();

    public boolean isValid() {
        return expiresAt == null || expiresAt.isAfter(Instant.now());
    }

    public boolean isPersistent() {
        return persistent;
    }

    /**
     * Unassign the entitlement from the guild.
     * If the {@link #target()} is {@link SkuTarget#GUILD} the guild cant be unassigned.
     */
    public abstract boolean unassignFromGuild();

    /**
     * Assign the entitlement to a guild.
     * If the {@link #target()} is {@link SkuTarget#GUILD} the guild cant be changed.
     *
     * @param guildId the guild id to assign the entitlement to
     */
    public abstract boolean assignToGuild(long guildId);
}
