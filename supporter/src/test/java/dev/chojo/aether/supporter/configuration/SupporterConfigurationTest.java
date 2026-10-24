/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.supporter.configuration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import dev.chojo.aether.supporter.configuration.modules.dummy.Empty;
import dev.chojo.aether.supporter.configuration.modules.subscriptions.SubscriptionKey;
import dev.chojo.aether.supporter.configuration.modules.subscriptions.platform.Platform;
import dev.chojo.aether.supporter.configuration.modules.subscriptions.platform.purchase.PurchaseType;
import dev.chojo.aether.supporter.jackson.SupporterModule;
import dev.chojo.aether.supporter.registry.SupporterRegistry;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SupporterConfigurationTest {
    enum FeatureID {}

    private static final String CONFIG = """
            {"subscriptions": {
               "KOFI_ONLY": {"name": "Ko-fi only", "id": 1, "platforms": {"KOFI": {"ids": {"SUBSCRIPTION": "Tier"}}}},
               "BOTH": {"name": "Both", "id": 2, "platforms": {
                  "KOFI": {"ids": {"SUBSCRIPTION": "Gold"}},
                  "DISCORD": {"ids": {"SUBSCRIPTION": "100", "LIFETIME": "200"}}}}},
             "features": {},
             "interactions": {"slash": {}, "messages": {}, "users": {}}}
            """;

    private static SupporterConfiguration<FeatureID, Empty, Empty> configuration;

    @BeforeAll
    static void read() {
        SupporterRegistry.SUBSCRIPTION_TYPES
                .register(new SubscriptionKey("KOFI_ONLY"))
                .register(new SubscriptionKey("BOTH"));
        JsonMapper mapper = JsonMapper.builder()
                .addModule(new SupporterModule())
                .changeDefaultVisibility(checker -> checker.withFieldVisibility(JsonAutoDetect.Visibility.ANY))
                .build();
        configuration = mapper.readValue(CONFIG, new TypeReference<>() {});
    }

    @Test
    void skipsSubscriptionsWithoutThePlatform() {
        assertEquals(
                2,
                configuration
                        .findSubscriptionBySkuID(Platform.DISCORD, "200")
                        .orElseThrow()
                        .id());
        assertEquals(
                PurchaseType.LIFETIME,
                configuration.findPurchaseTypeBySkuID(Platform.DISCORD, "200").orElseThrow());
        assertEquals(
                2,
                configuration
                        .findSubscription(Platform.DISCORD, PurchaseType.SUBSCRIPTION, "100")
                        .orElseThrow()
                        .id());
    }

    @Test
    void skipsSubscriptionsWithoutThePurchaseType() {
        assertTrue(configuration
                .findSubscription(Platform.KOFI, PurchaseType.LIFETIME, "Gold")
                .isEmpty());
        assertEquals(
                2,
                configuration
                        .findSubscription(Platform.KOFI, PurchaseType.SUBSCRIPTION, "Gold")
                        .orElseThrow()
                        .id());
    }
}
