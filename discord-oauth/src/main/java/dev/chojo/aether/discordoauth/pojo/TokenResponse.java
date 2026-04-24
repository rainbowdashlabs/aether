/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.discordoauth.pojo;

import dev.chojo.aether.discordoauth.access.IOAuthToken;
import dev.chojo.aether.discordoauth.access.OAuthScope;

import java.time.Instant;
import java.util.Set;

/**
 * Represents the response from a token exchange or refresh.
 *
 * @param accessToken  The access token used for API requests
 * @param refreshToken The refresh token used to get a new access token
 * @param expiry       The time when the access token expires
 */
public record TokenResponse(String accessToken, String refreshToken, Instant expiry, Set<OAuthScope> oAuthScopes)
        implements IOAuthToken {
    /**
     * Not supported
     *
     * @throws UnsupportedOperationException always
     */
    @Override
    public long userId() {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported
     *
     * @throws UnsupportedOperationException always
     */
    @Override
    public void update(TokenResponse response) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<OAuthScope> scopes() {
        return oAuthScopes;
    }

    /**
     * Not supported
     *
     * @throws UnsupportedOperationException always
     */
    @Override
    public void delete() {
        throw new UnsupportedOperationException();
    }
}
