/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.mailing.service;

import dev.chojo.aether.mailing.entities.MailSource;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Registry for {@link MailSource}s.
 */
public final class MailSourceRegistry {
    private static final Logger log = getLogger(MailSourceRegistry.class);
    private static final Set<MailSource> SOURCES = new HashSet<>();

    static {
        register(MailSource.DISCORD);
        register(MailSource.USER);
        register(MailSource.KOFI);
    }

    /**
     * Register a new {@link MailSource}.
     *
     * @param source the source to register
     */
    public static void register(MailSource source) {
        if (SOURCES.contains(source)) {
            throw new IllegalArgumentException("Source already registered");
        }
        log.info("Registered source {}", source.name());
        SOURCES.add(source);
    }

    /**
     * Get all registered {@link MailSource}s.
     *
     * @return an unmodifiable set of all registered sources
     */
    public static Set<MailSource> sources() {
        return Collections.unmodifiableSet(SOURCES);
    }

    /**
     * Get a {@link MailSource} by its name.
     *
     * @param name the name of the source
     * @return an optional containing the source if found
     */
    public Optional<MailSource> byName(String name) {
        return sources().stream()
                .filter(source -> source.name().equalsIgnoreCase(name))
                .findFirst();
    }
}
