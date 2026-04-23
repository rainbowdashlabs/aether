/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.discordoauth.pojo;

import dev.chojo.aether.discordoauth.access.IOAuthToken;

import java.util.List;

/**
 * Data for joining a guild.
 *
 * @param nick  Nickname to use in the guild. Requires the {@code MANAGE_NICKNAMES} permission
 * @param roles Roles to assign to the user. Requires the {@code MANAGE_ROLES} permission
 * @param mute  Whether to mute the user. Requires the {@code MUTE_MEMBERS} permission
 * @param deaf  Whether to deafen the user. Requires the {@code DEAFEN_MEMBERS} permission
 */
public record JoinGuildData(String nick, List<String> roles, boolean mute, boolean deaf) {
    public JoinGuildPayload withToken(IOAuthToken token) {
        return new JoinGuildPayload(token.accessToken(), nick(), roles(), mute(), deaf());
    }
}
