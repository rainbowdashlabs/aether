/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.mailing.entities;

public record MailSource(String name) {
    public static final MailSource DISCORD = MailSource.of("DISCORD");
    public static final MailSource USER = MailSource.of("USER");
    public static final MailSource KOFI = MailSource.of("KOFI");

    public static MailSource of(String name) {
        return new MailSource(name);
    }
}
