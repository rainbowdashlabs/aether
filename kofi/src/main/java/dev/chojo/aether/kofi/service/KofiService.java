/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.kofi.service;

import dev.chojo.aether.common.UserProvider;
import dev.chojo.aether.kofi.configuration.Kofi;
import dev.chojo.aether.kofi.exception.UnauthorizedException;
import dev.chojo.aether.kofi.pojo.KofiPurchase;
import dev.chojo.aether.kofi.pojo.KofiShopItem;
import dev.chojo.aether.kofi.pojo.KofiTransaction;
import dev.chojo.aether.kofi.pojo.SubscriptionResult;
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

public abstract class KofiService {
    private static final Logger log = LoggerFactory.getLogger(KofiService.class);
    private final Kofi configuration;
    private final UserProvider userProvider;
    private final SupporterConfiguration<?, ?, ?> supporterConfiguration;
    private final MailService mailService;
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public KofiService(
            Kofi configuration,
            UserProvider userProvider,
            MailService mailService,
            SupporterConfiguration<?, ?, ?> supporterConfiguration) {
        this.userProvider = userProvider;
        this.supporterConfiguration = supporterConfiguration;
        this.configuration = configuration;
        this.mailService = mailService;
        executorService.scheduleAtFixedRate(this::removeExpiredSubs, 40, 60, TimeUnit.MINUTES);
    }

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
            List<KofiPurchase> kofiPurchases = create(data);
            for (KofiPurchase kofiPurchase : kofiPurchases) {
                registerPurchase(kofiPurchase);
            }
        }
    }

    protected abstract void registerPurchase(KofiPurchase purchase);

    public SubscriptionResult enableSubscription(KofiPurchase purchase, Guild guild) {
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

        if (purchase.assignPurchaseToGuild(guild.getIdLong())) {
            return SubscriptionResult.SUCCESS;
        }
        return SubscriptionResult.UNKOWN;
    }

    public boolean disableSubscription(KofiPurchase purchase) {
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

        return purchase.unassignPurchaseFromGuild();
    }

    private Optional<User> resolveUser(KofiTransaction data) {
        if (data.discordUserId() == null) return Optional.empty();
        return userProvider.byId(data.discordUserId());
    }

    private void removeExpiredSubs() {
        for (KofiPurchase kofiPurchase : expiredPurchases()) {
            disableSubscription(kofiPurchase);
            kofiPurchase.delete();
        }
    }

    protected abstract List<KofiPurchase> expiredPurchases();

    public List<KofiPurchase> create(KofiTransaction transaction) {
        List<KofiPurchase> purchases = new ArrayList<>();

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

    protected abstract Subscriptions guildSubscriptions(long guildId);

    public abstract KofiPurchase buildPurchase(
            String mailHash, String transactionId, String key, Type type, long subscriptionId, Instant expiresAt);
}
