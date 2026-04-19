/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.supporter.service;

import com.google.inject.Inject;
import dev.chojo.aether.supporter.access.DiscordPurchase;
import dev.chojo.aether.supporter.access.SkuTarget;
import dev.chojo.aether.supporter.access.Subscription;
import dev.chojo.aether.supporter.access.Subscriptions;
import dev.chojo.aether.supporter.configuration.SupporterConfiguration;
import dev.chojo.aether.supporter.configuration.modules.subscriptions.platform.Platform;
import dev.chojo.aether.supporter.configuration.modules.subscriptions.platform.purchase.PurchaseType;
import dev.chojo.aether.supporter.service.context.SubscriptionResult;
import net.dv8tion.jda.api.entities.Entitlement;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.entitlement.EntitlementCreateEvent;
import net.dv8tion.jda.api.events.entitlement.EntitlementDeleteEvent;
import net.dv8tion.jda.api.events.entitlement.EntitlementUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static dev.chojo.aether.supporter.access.SkuTarget.GUILD;
import static dev.chojo.aether.supporter.configuration.modules.subscriptions.platform.Platform.DISCORD;
import static net.dv8tion.jda.api.entities.Entitlement.EntitlementType.APPLICATION_SUBSCRIPTION;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Base class for handling Discord Entitlements and managing subscriptions based on them.
 * <p>
 * This service implements {@link ListenerAdapter} to react to Discord entitlement events
 * (create, update, delete). It provides the glue between Discord's monetization system
 * and the supporter module's subscription management.
 *
 * @param <V> The type of Discord purchase handled by this service.
 */
public abstract class EntitlementService<V extends DiscordPurchase> extends ListenerAdapter {
    private static final Logger log = getLogger(EntitlementService.class);
    private final SupporterConfiguration<?, ?, ?> configuration;
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    @Inject
    public EntitlementService(SupporterConfiguration<?, ?, ?> configuration) {
        this.configuration = configuration;
        start();
    }

    protected void start() {
        executorService.scheduleAtFixedRate(this::processExpiredPurchases, 30, 60, TimeUnit.MINUTES);
    }

    private void processExpiredPurchases() {
        for (V expiredPurchase : getExpiredPurchases()) {
            disableSubscription(expiredPurchase);
        }
    }

    @Override
    public void onEntitlementCreate(@NonNull EntitlementCreateEvent event) {
        handleEntitlement(event.getEntitlement(), this::registerPurchase).ifPresent(purchase -> {
            if (purchase.target() == GUILD) {
                Guild guild = event.getJDA().getGuildById(purchase.guildId());
                if (guild != null) {
                    enableSubscription(purchase, guild);
                } else {
                    log.error("Could not find guild {} for purchase {}", purchase.guildId(), purchase.entitlementId());
                }
            }
        });
    }

    @Override
    public void onEntitlementDelete(@NonNull EntitlementDeleteEvent event) {
        handleEntitlement(event.getEntitlement(), this::unregisterPurchase).ifPresent(this::disableSubscription);
    }

    @Override
    public void onEntitlementUpdate(@NonNull EntitlementUpdateEvent event) {
        handleEntitlement(event.getEntitlement(), this::updatePurchase).ifPresent(purchase -> {
            if (purchase.guildId() == 0) return;
            Guild guild = event.getJDA().getGuildById(purchase.guildId());
            if (guild != null) {
                enableSubscription(purchase, guild);
            } else {
                log.error("Could not find guild {} for purchase {}", purchase.guildId(), purchase.entitlementId());
            }
        });
    }

    /**
     * Refreshes the entitlements for a guild.
     * <p>
     * This will clear all existing Discord-based subscriptions for the guild and
     * re-register all currently active entitlements retrieved from Discord.
     *
     * @param guild The guild to refresh.
     */
    public void refresh(Guild guild) {
        List<Entitlement> entitlements = guild.getJDA()
                .retrieveEntitlements()
                .guild(guild.getIdLong())
                .excludeEnded(true)
                .complete();
        Subscriptions subscriptions = guildSubscriptions(guild.getIdLong());
        subscriptions.clear(DISCORD);
        for (Entitlement entitlement : entitlements) {
            handleEntitlement(entitlement, this::registerPurchase);
        }
    }

    /**
     * Enables a subscription for a guild based on a purchase.
     *
     * @param purchase The purchase to enable.
     * @param guild    The guild to enable the subscription for.
     * @return The result of the subscription attempt.
     */
    public SubscriptionResult enableSubscription(V purchase, Guild guild) {
        // The purchase is tied to a guild and can't be assigned to another guild.
        if (purchase.target() == GUILD && purchase.guildId() != guild.getIdLong()) return SubscriptionResult.LOCKED;
        // The purchase is expired and should be cleaned up soon.
        if (!purchase.isValid()) return SubscriptionResult.SUBSCRIPTION_EXPIRED;

        Subscriptions subs = guildSubscriptions(guild.getIdLong());
        if (subs.hasAccess(purchase.subscriptionId())) return SubscriptionResult.ALREADY_SUBSCRIBED;

        if (purchase.guildId() != guild.getIdLong() && purchase.guildId() != 0) disableSubscription(purchase);

        subs.addSubscription(new Subscription(
                purchase.subscriptionId(),
                guild.getIdLong(),
                DISCORD,
                GUILD,
                APPLICATION_SUBSCRIPTION,
                purchase.expiresAt(),
                purchase.isPersistent()));

        if (purchase.assignToGuild(guild.getIdLong())) {
            return SubscriptionResult.SUCCESS;
        }
        return SubscriptionResult.UNKOWN;
    }

    /**
     * Disables a subscription based on a purchase.
     *
     * @param purchase The purchase to disable.
     * @return {@code true} if the subscription was successfully disabled.
     */
    public boolean disableSubscription(V purchase) {
        // Nothing to disable here
        if (purchase.guildId() == 0) return false;
        Subscriptions subs = guildSubscriptions(purchase.guildId());

        subs.deleteSubscription(new Subscription(
                purchase.subscriptionId(), purchase.guildId(), DISCORD, GUILD, APPLICATION_SUBSCRIPTION, null, true));

        return purchase.unassignFromGuild();
    }

    /**
     * Build a purchase from the entitlement data
     *
     * @param entitlement    entitlement
     * @param subscriptionId subscription id of the entitlement
     * @return purchase
     */
    protected V buildPurchase(Entitlement entitlement, long subscriptionId, PurchaseType purchaseType) {
        return buildPurchase(
                entitlement.getUserIdLong(),
                entitlement.getSkuIdLong(),
                entitlement.getType(),
                SkuTarget.fromEntitlement(entitlement),
                subscriptionId,
                entitlement.getIdLong(),
                entitlement.getTimeEnding().toInstant(),
                entitlement.getGuildIdLong(),
                purchaseType);
    }

    /**
     * Build a purchase from the entitlement data
     *
     * @param userId         The user id of the user that owns this entitlement.
     * @param skuId          The id of the sku that this entitlement is for.
     * @param type           The type of entitlement
     * @param target         The target of the entitlement.
     * @param subscriptionId Id of the subscription that this entitlement is for.
     * @param entitlementId  The id of the entitlement
     * @param expiresAt      The time the entitlement expires
     * @param guildId        The id of the guild that the purchase it attached to.
     * @param purchaseType   the type of the purchase
     * @return The purchase
     */
    protected abstract V buildPurchase(
            long userId,
            long skuId,
            Entitlement.EntitlementType type,
            SkuTarget target,
            long subscriptionId,
            long entitlementId,
            @Nullable Instant expiresAt,
            long guildId,
            PurchaseType purchaseType);

    /**
     * Register a purchase at the persistent storage.
     * This does not create a subscription.
     *
     * @param purchase The purchase to register.
     * @return The registered purchase.
     */
    protected abstract V registerPurchase(V purchase);

    /**
     * Update a purchase at the persistent storage.
     * This does not create a subscription.
     * <p>
     * A purchase can be uniquely identified by its {@link V#entitlementId()}
     *
     * @param purchase The purchase to register.
     * @return The registered purchase.
     */
    protected abstract V updatePurchase(V purchase);

    /**
     * Unregister a purchase from the persistent storage.
     * <p>
     * This does not remove the subscription.
     *
     * @param purchase the purchase to unregister.
     * @return the unregistered purchase.
     */
    protected abstract V unregisterPurchase(V purchase);

    /**
     * Retrieves the subscriptions for a guild.
     *
     * @param guildId The ID of the guild.
     * @return The subscriptions.
     */
    protected abstract Subscriptions guildSubscriptions(long guildId);

    private Optional<V> handleEntitlement(Entitlement ent, Function<V, V> func) {
        var subscription = configuration.findSubscriptionBySkuID(DISCORD, ent.getSkuId());
        Optional<V> value = subscription.map(sub -> {
            var type = sub.platformSubscription(DISCORD)
                    .subscriptionType(ent.getSkuId())
                    .get();
            V v = buildPurchase(ent, sub.id(), type);
            return func.apply(v);
        });
        if (value.isEmpty()) {
            log.error("Unknown sku {} for {}", ent.getSkuId(), ent.getUserId());
        }
        return value;
    }

    private Optional<PurchaseType> getPurchaseType(V entitlement) {
        return configuration.findPurchaseTypeBySkuID(Platform.DISCORD, String.valueOf(entitlement.skuId()));
    }

    /**
     * Get all expired purchases that are still assigned to a guild
     *
     * @return list of expired purchases
     * @see DiscordPurchase#expiresAt()
     * @see DiscordPurchase#guildId()
     */
    protected abstract List<V> getExpiredPurchases();
}
