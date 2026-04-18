/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.supporter.configuration.modules.subscriptions;

import dev.chojo.aether.supporter.registry.Key;

/**
 * Represents a unique identifier for a subscription.
 * Each subscription needs a unique identifier.
 */
public record SubscriptionKey(String name) implements Key {}
