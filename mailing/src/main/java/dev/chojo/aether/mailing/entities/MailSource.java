/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.mailing.entities;

import dev.chojo.aether.common.registry.Key;

/**
 * Represents the source of a mail registration.
 *
 * @param name the name of the source
 */
public record MailSource(String name) implements Key {
    public static final MailSource DISCORD = MailSource.of("DISCORD");
    public static final MailSource USER = MailSource.of("USER");
    public static final MailSource KOFI = MailSource.of("KOFI");

    /**
     * Create a new {@link MailSource} with the given name.
     *
     * @param name the name
     * @return the mail source
     */
    public static MailSource of(String name) {
        return new MailSource(name);
    }
}
