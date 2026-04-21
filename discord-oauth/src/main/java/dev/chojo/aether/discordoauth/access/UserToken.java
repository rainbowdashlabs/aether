/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.discordoauth.access;

import dev.chojo.aether.discordoauth.pojo.TokenResponse;

/**
 * A token associated with a user.
 */
public interface UserToken {
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
     * Delete the token.
     */
    void delete();
}
