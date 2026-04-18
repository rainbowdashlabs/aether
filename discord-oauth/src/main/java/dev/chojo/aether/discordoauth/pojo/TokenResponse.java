/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.discordoauth.pojo;

import java.time.Instant;

/**
 * Represents the response from a token exchange or refresh.
 *
 * @param accessToken  The access token used for API requests
 * @param refreshToken The refresh token used to get a new access token
 * @param expiry       The time when the access token expires
 */
public record TokenResponse(String accessToken, String refreshToken, Instant expiry) {}
