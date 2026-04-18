/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.kofi.configuration;

/**
 * Configuration for Ko-fi integration.
 */
public class Kofi {
    private String token;

    /**
     * @return The verification token for Ko-fi webhooks.
     */
    public String token() {
        return token;
    }
}
