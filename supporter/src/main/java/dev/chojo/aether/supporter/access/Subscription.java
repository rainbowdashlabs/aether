/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.supporter.access;

import dev.chojo.aether.supporter.configuration.modules.subscriptions.platform.Platform;
import net.dv8tion.jda.api.entities.Entitlement;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Optional;

public class Subscription {
    /**
     * The id this subscription is targeted.
     * This is a user id, if the {@link #target} is {@link SkuTarget#USER}
     * This is a guild id, if the {@link #target} is {@link SkuTarget#GUILD}
     */
    private final long targetId;

    /**
     * ID of the {@link dev.chojo.aether.supporter.configuration.modules.subscriptions.Subscription#id()}
     */
    private final long subscriptionId;

    /**
     * The platform this subscription was purchased on
     */
    private final Platform source;

    /**
     * The target of the subscription
     */
    private final SkuTarget target;

    private final Entitlement.EntitlementType purchaseType;
    private final Instant endsAt;
    private final boolean persistent;

    public Subscription(
            long subscriptionId,
            long targetId,
            Platform source,
            SkuTarget target,
            Entitlement.EntitlementType purchaseType,
            Instant endsAt,
            boolean persistent) {
        this.subscriptionId = subscriptionId;
        this.targetId = targetId;
        this.source = source;
        this.target = target;
        this.purchaseType = purchaseType;
        this.endsAt = endsAt;
        this.persistent = persistent;
    }

    public static Subscription fromEntitlement(Entitlement entitlement) {
        return fromEntitlement(entitlement, false);
    }

    public static Subscription fromEntitlement(Entitlement entitlement, boolean persistent) {
        SkuTarget targetType = SkuTarget.fromEntitlement(entitlement);
        long target = entitlement.getGuildId() == null ? entitlement.getUserIdLong() : entitlement.getGuildIdLong();

        return new Subscription(
                entitlement.getSkuIdLong(),
                target,
                Platform.DISCORD,
                targetType,
                entitlement.getType(),
                Optional.ofNullable(entitlement.getTimeEnding())
                        .map(OffsetDateTime::toInstant)
                        .orElse(null),
                persistent);
    }

    public long targetId() {
        return targetId;
    }

    public long subscriptionId() {
        return subscriptionId;
    }

    public SkuTarget target() {
        return target;
    }

    public Instant endsAt() {
        return endsAt;
    }

    public Entitlement.EntitlementType purchaseType() {
        return purchaseType;
    }

    public boolean isPersistent() {
        return persistent;
    }

    public Platform source() {
        return source;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Subscription that = (Subscription) o;
        return targetId == that.targetId && target == that.target;
    }

    @Override
    public int hashCode() {
        int result = Long.hashCode(targetId);
        result = 31 * result + target.hashCode();
        return result;
    }
}
