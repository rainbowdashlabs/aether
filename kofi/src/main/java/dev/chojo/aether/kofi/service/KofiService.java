/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.kofi.service;

import dev.chojo.aether.common.provider.UserProvider;
import dev.chojo.aether.kofi.configuration.Kofi;
import dev.chojo.aether.kofi.exception.UnauthorizedException;
import dev.chojo.aether.kofi.pojo.KofiPurchase;
import dev.chojo.aether.kofi.pojo.KofiShopItem;
import dev.chojo.aether.kofi.pojo.KofiTransaction;
import dev.chojo.aether.kofi.pojo.Type;
import dev.chojo.aether.mailing.UserMails;
import dev.chojo.aether.mailing.entities.FailureReason;
import dev.chojo.aether.mailing.entities.MailEntry;
import dev.chojo.aether.mailing.entities.MailSource;
import dev.chojo.aether.mailing.entities.Result;
import dev.chojo.aether.mailing.service.MailService;
import dev.chojo.aether.supporter.access.Subscription;
import dev.chojo.aether.supporter.access.Subscriptions;
import dev.chojo.aether.supporter.configuration.SupporterConfiguration;
import dev.chojo.aether.supporter.configuration.modules.subscriptions.platform.purchase.PurchaseType;
import dev.chojo.aether.supporter.service.context.SubscriptionResult;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static dev.chojo.aether.supporter.access.SkuTarget.GUILD;
import static dev.chojo.aether.supporter.configuration.modules.subscriptions.platform.Platform.KOFI;
import static net.dv8tion.jda.api.entities.Entitlement.EntitlementType.APPLICATION_SUBSCRIPTION;

/**
 * Service for handling Ko-fi webhooks and managing purchases and subscriptions.
 */
public abstract class KofiService<V extends KofiPurchase> {
    private static final Logger log = LoggerFactory.getLogger(KofiService.class);
    private final Kofi configuration;
    private final UserProvider userProvider;
    private final SupporterConfiguration<?, ?, ?> supporterConfiguration;
    private final MailService mailService;
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    /**
     * Creates a new KofiService.
     *
     * @param configuration          The Ko-fi configuration.
     * @param userProvider           The user provider for resolving Discord users.
     * @param mailService            The mail service for managing user emails and sending notifications.
     * @param supporterConfiguration The supporter configuration for mapping products to subscriptions.
     */
    public KofiService(
            Kofi configuration,
            UserProvider userProvider,
            MailService mailService,
            SupporterConfiguration<?, ?, ?> supporterConfiguration) {
        this.userProvider = userProvider;
        this.supporterConfiguration = supporterConfiguration;
        this.configuration = configuration;
        this.mailService = mailService;
    }

    protected void start() {
        executorService.scheduleAtFixedRate(this::removeExpiredSubs, 40, 60, TimeUnit.MINUTES);
    }

    /**
     * Handles a Ko-fi transaction from a webhook.
     *
     * @param data The transaction data.
     * @throws UnauthorizedException If the verification token is invalid.
     */
    public void handle(KofiTransaction data) {
        if (!configuration.token().equals(data.verificationToken())) {
            throw new UnauthorizedException();
        }

        String mailHash = mailService.mailHash(data.email());

        // Check whether we know the user already that purchased something.
        Optional<UserMails> repUser = mailService.mailProvider().byHash(mailHash);
        if (repUser.isEmpty()) {
            Optional<User> user = resolveUser(data);
            if (user.isPresent()) {
                // Register email adress for that user if there is a registered discord account.
                Result<MailEntry, FailureReason> result =
                        mailService.registerVerifiedMail(user.get().getIdLong(), data.email(), MailSource.KOFI);
                if (result.isFailure()) {
                    log.error(
                            "Could not register verified mail {} for user {}: {}",
                            data.email(),
                            user.get().getIdLong(),
                            result.failureReason());
                }
            } else {
                mailService.sendMail(mailService.templates().kofiUserNotFound(data.email(), mailService.host()));
            }
        }

        if (data.type() == Type.SHOP_ORDER || data.type() == Type.SUBSCRIPTION) {
            List<V> kofiPurchases = create(data);
            for (V kofiPurchase : kofiPurchases) {
                registerPurchase(kofiPurchase);
            }
        }
    }

    /**
     * Registers a purchase in the persistent storage.
     *
     * @param purchase The purchase to register.
     */
    protected abstract void registerPurchase(V purchase);

    /**
     * Enables a subscription for a guild based on a purchase.
     *
     * @param purchase The purchase to use.
     * @param guild    The guild to enable the subscription for.
     * @return The result of the operation.
     */
    public SubscriptionResult enableSubscription(V purchase, Guild guild) {
        Subscriptions subs = guildSubscriptions(guild.getIdLong());

        if (purchase.type() == Type.SUBSCRIPTION) {
            if (!purchase.isValid()) return SubscriptionResult.SUBSCRIPTION_EXPIRED;
            if (subs.hasAccess(purchase.subscriptionId())) return SubscriptionResult.ALREADY_SUBSCRIBED;
            if (purchase.guildId() != 0) disableSubscription(purchase);
            subs.addSubscription(new Subscription(
                    purchase.subscriptionId(),
                    guild.getIdLong(),
                    KOFI,
                    GUILD,
                    APPLICATION_SUBSCRIPTION,
                    purchase.expiresAt(),
                    true));
        } else if (purchase.type() == Type.SHOP_ORDER) {
            var sub = supporterConfiguration.byId(purchase.subscriptionId());
            if (sub.isEmpty()) {
                log.error("Could not find subscription for lifetime sku {}", purchase.subscriptionId());
                return SubscriptionResult.UNKOWN;
            }
            if (subs.hasAccess(sub.get().id())) return SubscriptionResult.ALREADY_SUBSCRIBED;
            if (purchase.guildId() != 0) disableSubscription(purchase);
            subs.addSubscription(new Subscription(
                    sub.get().id(), guild.getIdLong(), KOFI, GUILD, APPLICATION_SUBSCRIPTION, null, true));
        } else {
            // This should never happen
            log.error("Unknown purchase type {}", purchase.type());
            return SubscriptionResult.UNKOWN;
        }

        if (purchase.assignToGuild(guild.getIdLong())) {
            return SubscriptionResult.SUCCESS;
        }
        return SubscriptionResult.UNKOWN;
    }

    /**
     * Disables a subscription for a guild.
     *
     * @param purchase The purchase to disable.
     * @return {@code true} if successful, {@code false} otherwise.
     */
    public boolean disableSubscription(V purchase) {
        if (purchase.guildId() == 0) return false;
        Subscriptions subs = guildSubscriptions(purchase.guildId());
        if (purchase.type() == Type.SUBSCRIPTION) {
            subs.deleteSubscription(new Subscription(
                    purchase.subscriptionId(), purchase.guildId(), KOFI, GUILD, APPLICATION_SUBSCRIPTION, null, true));
        }
        if (purchase.type() == Type.SHOP_ORDER) {
            var sub = supporterConfiguration.byId(purchase.subscriptionId());
            if (sub.isEmpty()) {
                log.error("Could not find subscription for lifetime sku {}", purchase.subscriptionId());
                return false;
            }
            subs.deleteSubscription(new Subscription(
                    sub.get().id(), purchase.guildId(), KOFI, GUILD, APPLICATION_SUBSCRIPTION, null, true));
        }

        return purchase.unassignFromGuild();
    }

    private Optional<User> resolveUser(KofiTransaction data) {
        if (data.discordUserId() == null) return Optional.empty();
        return userProvider.byId(data.discordUserId());
    }

    private void removeExpiredSubs() {
        for (V kofiPurchase : expiredPurchases()) {
            disableSubscription(kofiPurchase);
            kofiPurchase.delete();
        }
    }

    /**
     * @return A list of expired purchases that should be removed.
     */
    protected abstract List<V> expiredPurchases();

    /**
     * Creates {@link KofiPurchase} objects from a {@link KofiTransaction}.
     *
     * @param transaction The transaction to process.
     * @return A list of created purchases.
     */
    public List<V> create(KofiTransaction transaction) {
        List<V> purchases = new ArrayList<>();

        String mailHash = mailService.mailHash(transaction.email());
        Type type = transaction.type();
        if (type == Type.SUBSCRIPTION) {
            var id = supporterConfiguration
                    .findSubscription(KOFI, PurchaseType.SUBSCRIPTION, transaction.tierName())
                    .orElseThrow()
                    .id();
            purchases.add(buildPurchase(
                    mailHash,
                    transaction.kofiTransactionId(),
                    transaction.tierName(),
                    type,
                    id,
                    Instant.now().plus(32, ChronoUnit.DAYS)));
        } else if (type == Type.SHOP_ORDER) {
            int num = 0;
            for (KofiShopItem shopItem : transaction.shopItems()) {
                var id = supporterConfiguration
                        .findSubscription(KOFI, PurchaseType.LIFETIME, shopItem.directLinkCode())
                        .orElseThrow()
                        .id();
                for (int i = 0; i < shopItem.quantity(); i++) {
                    purchases.add(buildPurchase(
                            mailHash,
                            transaction.kofiTransactionId() + "-" + (num++),
                            shopItem.directLinkCode(),
                            type,
                            id,
                            null));
                }
            }
        }
        return purchases;
    }

    /**
     * Retrieves the subscriptions for a guild.
     *
     * @param guildId The ID of the guild.
     * @return The subscriptions.
     */
    protected abstract Subscriptions guildSubscriptions(long guildId);

    /**
     * Builds a {@link KofiPurchase} object.
     *
     * @param mailHash       The mail hash of the purchaser.
     * @param transactionId  The transaction ID.
     * @param key            The key of the product (tier name or shop item code).
     * @param type           The type of purchase.
     * @param subscriptionId The ID of the associated subscription.
     * @param expiresAt      The expiration date (for subscriptions).
     * @return The created purchase object.
     */
    protected abstract V buildPurchase(
            String mailHash, String transactionId, String key, Type type, long subscriptionId, Instant expiresAt);
}
