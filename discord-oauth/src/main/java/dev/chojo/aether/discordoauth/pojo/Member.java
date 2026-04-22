/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.discordoauth.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

public record Member(
        User user,
        String nick,
        String avatar,
        String banner,
        List<String> roles,
        @JsonProperty("joined_at") Instant joinedAt,
        @JsonProperty("premium_since") Instant premiumSince) {}
