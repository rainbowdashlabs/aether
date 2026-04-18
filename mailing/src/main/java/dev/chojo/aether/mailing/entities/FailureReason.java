/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.mailing.entities;

public enum FailureReason {
    /**
     * The email is already registered to another user.
     */
    ALREADY_REGISTERED,
    /**
     * The email has an invalid formal
     */
    INVALID_FORMAT,
    /**
     * The email that should be verified is connected to another user.
     */
    WRONG_USER,
    /**
     * The code provided for verification is invalid
     */
    INVALID_CODE,
    /**
     * The code provided has already expired.
     */
    CODE_EXPIRED,
    /**
     * The address that should be verified is unknown
     */
    UNKNOWN_ADDRESS,
    /**
     * Too many requests for registration.
     */
    RATE_LIMIT
}
