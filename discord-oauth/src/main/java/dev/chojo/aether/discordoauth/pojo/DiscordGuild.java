/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.discordoauth.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a Discord guild (server).
 *
 * @param id             The guild ID
 * @param name           The guild name
 * @param icon           The guild icon hash
 * @param permissions    The user's permissions in this guild (old format)
 * @param permissionsNew The user's permissions in this guild (new format)
 * @param owner          Whether the user is the owner of the guild
 */
public record DiscordGuild(
        String id,
        String name,
        String icon,
        String permissions,
        @JsonProperty("permissions_new") String permissionsNew,
        boolean owner) {}
