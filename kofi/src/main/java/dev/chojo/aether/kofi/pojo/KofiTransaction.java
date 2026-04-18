/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.kofi.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

/**
 * Represents a transaction received from a Ko-fi webhook.
 *
 * @param verificationToken          The verification token to validate the request.
 * @param messageId                  The unique message ID.
 * @param timestamp                  The timestamp of the transaction.
 * @param type                       The type of the transaction (e.g., Subscription, Donation).
 * @param isPublic                   Whether the transaction is public.
 * @param fromName                   The name of the sender.
 * @param message                    The message attached to the transaction.
 * @param amount                     The amount of the transaction.
 * @param url                        The URL of the transaction.
 * @param email                      The email of the sender.
 * @param currency                   The currency of the transaction.
 * @param isSubscriptionPayment      Whether it is a subscription payment.
 * @param isFirstSubscriptionPayment Whether it is the first payment of a subscription.
 * @param kofiTransactionId          The Ko-fi transaction ID.
 * @param shopItems                  List of items purchased if it's a shop order.
 * @param shipping                   Shipping information.
 * @param tierName                   The name of the subscription tier, if applicable.
 * @param discordUsername            The Discord username of the sender, if provided.
 * @param discordUserId              The Discord user ID of the sender, if provided.
 */
@JsonIgnoreProperties({"shipping"})
public record KofiTransaction(
        @JsonProperty("verification_token") String verificationToken,
        @JsonProperty("message_id") String messageId,
        Instant timestamp,
        Type type,
        @JsonProperty("is_public") boolean isPublic,
        @JsonProperty("from_name") String fromName,
        String message,
        String amount,
        String url,
        String email,
        String currency,
        @JsonProperty("is_subscription_payment") boolean isSubscriptionPayment,
        @JsonProperty("is_first_subscription_payment") boolean isFirstSubscriptionPayment,
        @JsonProperty("kofi_transaction_id") String kofiTransactionId,
        @JsonProperty("shop_items") List<KofiShopItem> shopItems,
        @JsonProperty("shipping") Object shipping,
        @JsonProperty("tier_name") String tierName,
        @JsonProperty("discord_username") String discordUsername,
        @JsonProperty("discord_userid") String discordUserId) {}
