/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.discordoauth.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record Connection(
        String id,
        String name,
        ConnectionType type,
        boolean revoked,
        List<ConnectionType> integrations,
        boolean verified,
        @JsonProperty("friend_sync") boolean friendSync,
        @JsonProperty("show_activity") boolean showActivity,
        @JsonProperty("two_way_link") boolean twoWayLink,
        @JsonProperty("visibility") int visibility) {}
