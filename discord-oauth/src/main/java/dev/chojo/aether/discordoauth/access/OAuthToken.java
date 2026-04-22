/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.discordoauth.access;

import dev.chojo.aether.discordoauth.pojo.TokenResponse;

import java.time.Instant;
import java.util.Set;

/**
 * A token associated with a user.
 */
public interface OAuthToken {
    /**
     * User id associated with the token
     *
     * @return user id
     */
    long userId();

    /**
     * Update the token with the new data.
     *
     * @param response the new token data
     */
    void update(TokenResponse response);

    /**
     * Get the refresh token associated with the token.
     *
     * @return refresh token
     */
    String refreshToken();

    /**
     * Get the scopes associated with the token.
     *
     * @return scopes
     */
    Set<OAuthScope> scopes();

    /**
     * Returns true if the token has the specified scope
     *
     * @param OAuthScope the scope to check
     * @return true if the token has the specified scope
     */
    default boolean hasScope(OAuthScope OAuthScope) {
        return scopes().contains(OAuthScope);
    }

    /**
     * Get the access token associated with the token.
     *
     * @return access token
     */
    String accessToken();

    /**
     * Get the time at which the token expires.
     *
     * @return expiration time
     */
    Instant expiry();

    /**
     * Delete the token.
     */
    void delete();
}
