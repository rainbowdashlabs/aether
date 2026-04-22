/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.mailing.service;

import dev.chojo.aether.common.registry.Registry;
import dev.chojo.aether.mailing.entities.MailSource;

/**
 * Registry for {@link MailSource}s.
 */
public final class MailSourceRegistry extends Registry<MailSource> {
    public static final MailSourceRegistry INSTANCE = new MailSourceRegistry();

    static {
        INSTANCE.register(MailSource.DISCORD).register(MailSource.USER).register(MailSource.KOFI);
    }

    public MailSourceRegistry() {
        super(MailSource.class);
    }
}
