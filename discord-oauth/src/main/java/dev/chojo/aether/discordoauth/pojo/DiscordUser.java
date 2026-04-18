/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.discordoauth.pojo;

/**
 * Represents a Discord user.
 *
 * @param id    The unique Discord user ID
 * @param email The user's email address (requires 'email' scope)
 */
public record DiscordUser(long id, String email) {}
