/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.kofi.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties({"variation_name"})
public record KofiShopItem(
        @JsonProperty("direct_link_code") String directLinkCode,
        @JsonProperty("item_name") String itemName,
        @JsonProperty("quantity") int quantity) {}
