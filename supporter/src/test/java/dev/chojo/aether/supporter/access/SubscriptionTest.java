/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.supporter.access;

import dev.chojo.aether.supporter.configuration.modules.subscriptions.platform.Platform;
import net.dv8tion.jda.api.entities.Entitlement.EntitlementType;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SubscriptionTest {
    @Test
    void equalWhenSameSubscriptionTargetAndPlatform() {
        var stored = new Subscription(
                1, 42, Platform.KOFI, SkuTarget.GUILD, EntitlementType.APPLICATION_SUBSCRIPTION, Instant.now(), false);
        var lookup = new Subscription(1, 42, Platform.KOFI, SkuTarget.GUILD, EntitlementType.PURCHASE, null, true);
        assertEquals(stored, lookup);
        assertEquals(stored.hashCode(), lookup.hashCode());

        List<Subscription> subscriptions = new ArrayList<>(List.of(stored));
        assertTrue(subscriptions.remove(lookup));
    }

    @Test
    void differentSubscriptionOrPlatformIsNotEqual() {
        var kofi = new Subscription(1, 42, Platform.KOFI, SkuTarget.GUILD, EntitlementType.PURCHASE, null, true);
        assertNotEquals(
                kofi, new Subscription(2, 42, Platform.KOFI, SkuTarget.GUILD, EntitlementType.PURCHASE, null, true));
        assertNotEquals(
                kofi, new Subscription(1, 42, Platform.DISCORD, SkuTarget.GUILD, EntitlementType.PURCHASE, null, true));
    }
}
