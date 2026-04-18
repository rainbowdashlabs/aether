/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.discordoauth.configuration;

/**
 * Controls the prompt behavior of the authorization page.
 */
public enum Prompt {
    /**
     * Do not show any prompt if the user has already authorized the application.
     */
    NONE,
    /**
     * Always show the consent prompt, even if the user has already authorized the application.
     */
    CONSENT
}
