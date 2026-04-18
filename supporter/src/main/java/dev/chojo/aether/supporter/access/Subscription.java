package dev.chojo.aether.supporter.access;

import dev.chojo.aether.supporter.configuration.modules.subscriptions.platform.Platform;
import net.dv8tion.jda.api.entities.Entitlement;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Optional;

public class Subscription {
    /// unique id for this subscription
    private final long id;
    /// ID of the [dev.chojo.aether.supporter.configuration.modules.subscriptions.Subscription]
    private final long subscriptionId;
    /// The platform this subscription was purchased on
    private final Platform source;
    /// The target of the subscription
    private final SkuTarget skuTarget;
    private final Entitlement.EntitlementType purchaseType;
    private final Instant endsAt;
    private final boolean persistent;

    public Subscription(
            long subscriptionId,
            long id,
            Platform source,
            SkuTarget skuTarget,
            Entitlement.EntitlementType purchaseType,
            Instant endsAt,
            boolean persistent) {
        this.subscriptionId = subscriptionId;
        this.id = id;
        this.source = source;
        this.skuTarget = skuTarget;
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

    public long id() {
        return id;
    }

    public SkuTarget skuTarget() {
        return skuTarget;
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
        return id == that.id && skuTarget == that.skuTarget;
    }

    @Override
    public int hashCode() {
        int result = Long.hashCode(id);
        result = 31 * result + skuTarget.hashCode();
        return result;
    }
}
