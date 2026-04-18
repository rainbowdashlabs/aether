/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.kofi.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Type {
    @JsonProperty("Subscription")
    SUBSCRIPTION,
    @JsonProperty("Donation")
    DONATION,
    @JsonProperty("Commission")
    COMMISSION,
    @JsonProperty("Shop Order")
    SHOP_ORDER
}
