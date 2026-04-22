/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.discordoauth.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public record User(
        String id,
        String username,
        String discriminator,
        @JsonProperty("global_name") String globalName,
        String avatar) {}
