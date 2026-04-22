/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.discordoauth.configuration;

import dev.chojo.aether.discordoauth.access.OAuthScope;

import java.util.Set;

/**
 * Configuration for Discord OAuth.
 */
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public class DiscordOAuth {
    private String clientId = "";
    private String clientSecret = "";
    private String redirectUri = "";
    private int cacheRetentionMinutes = 10;
    private Set<OAuthScope> OAuthScopes = Set.of(OAuthScope.IDENTIFY, OAuthScope.EMAIL, OAuthScope.GUILDS);
    private Prompt prompt = Prompt.NONE;

    /**
     * @return The Discord client ID
     */
    public String clientId() {
        return clientId;
    }

    /**
     * @return The Discord client secret
     */
    public String clientSecret() {
        return clientSecret;
    }

    /**
     * @return The redirect URI registered in the Discord developer portal
     */
    public String redirectUri() {
        return redirectUri;
    }

    /**
     * @return The number of minutes to retain user data in cache
     */
    public int cacheRetentionMinutes() {
        return cacheRetentionMinutes;
    }

    /**
     * @return The OAuth scopes to request, space-separated
     */
    public Set<OAuthScope> scopes() {
        return OAuthScopes;
    }

    /**
     * @return The prompt type for the authorization page
     */
    public Prompt prompt() {
        return prompt;
    }
}
