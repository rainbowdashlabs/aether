/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.supporter.registry;

import org.slf4j.Logger;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * A registry for {@link Key} implementations.
 * This allows for easy lookup and management of extensible keys like platforms or subscription types.
 *
 * @param <V> The type of the key.
 */
public class Registry<V extends Key> {
    private final Logger log = getLogger(Registry.class);
    private final Set<V> REGISTRY = new HashSet<>();
    private final Class<V> clazz;

    public Registry(Class<V> clazz) {
        this.clazz = clazz;
    }

    /**
     * Registers a new source in the registry.
     *
     * @param source The source to register.
     * @return The registry for chaining.
     * @throws IllegalArgumentException If the source is already registered.
     */
    public Registry<V> register(V source) {
        if (REGISTRY.contains(source)) {
            throw new IllegalArgumentException("Source already registered");
        }
        log.info("Registered source {} in {}Registry", source.name(), clazz.getSimpleName());
        REGISTRY.add(source);
        return this;
    }

    /**
     * Returns all registered sources.
     *
     * @return An unmodifiable set of all sources.
     */
    public Set<V> sources() {
        return Collections.unmodifiableSet(REGISTRY);
    }

    /**
     * Finds a source by its name.
     *
     * @param name The name of the source (case-insensitive).
     * @return An optional containing the source if found.
     */
    public Optional<V> byName(String name) {
        return sources().stream()
                .filter(source -> source.name().equalsIgnoreCase(name))
                .findFirst();
    }
}
