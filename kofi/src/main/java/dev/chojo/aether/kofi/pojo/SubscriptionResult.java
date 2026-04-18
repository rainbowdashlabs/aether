/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.kofi.pojo;

/**
 * Represents the result of an attempt to enable or disable a subscription.
 */
public enum SubscriptionResult {
    /**
     * The user is already subscribed to this tier.
     */
    ALREADY_SUBSCRIBED,
    /**
     * The subscription has expired and cannot be enabled.
     */
    SUBSCRIPTION_EXPIRED,
    /**
     * An unknown error occurred.
     */
    UNKOWN,
    /**
     * The subscription was successfully enabled.
     */
    SUCCESS
}
