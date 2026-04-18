/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.supporter.service;

/**
 * Keys used for the {@link io.github.kaktushose.jdac.dispatching.context.InvocationContext} key-value store.
 */
public class SupporterKeys {
    /**
     * The key for the {@link dev.chojo.aether.supporter.service.context.SupporterValidator}.
     */
    public static final String SUBSCRIPTION_VALIDATOR = "supporter_validator";
    /**
     * The key for the {@link dev.chojo.aether.supporter.service.context.SubscriptionContext}.
     */
    public static final String SUBSCRIPTION_CONTEXT = "supporter_validator_guild";
}
