/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.common.registry;

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
    private final Set<V> registry = new HashSet<>();
    private final Class<V> clazz;

    public Registry(Class<V> clazz) {
        this.clazz = clazz;
    }

    /**
     * Registers a new entry in the registry.
     *
     * @param entry The source to register.
     * @return The registry for chaining.
     * @throws IllegalArgumentException If the source is already registered.
     */
    public Registry<V> register(V entry) {
        if (registry.contains(entry)) {
            throw new IllegalArgumentException("Entry already registered");
        }
        log.info("Registered {} in {}Registry", entry.name(), clazz.getSimpleName());
        registry.add(entry);
        return this;
    }

    /**
     * Returns all registered sources.
     *
     * @return An unmodifiable set of all sources.
     */
    public Set<V> entries() {
        return Collections.unmodifiableSet(registry);
    }

    /**
     * Unregisters an entry from the registry.
     * @param entry The entry to unregister.
     */
    public void unregister(V entry) {
        registry.remove(entry);
        log.info("Unregistered {} in {}Registry", entry.name(), clazz.getSimpleName());
    }

    /**
     * Finds a source by its name.
     *
     * @param name The name of the source (case-insensitive).
     * @return An optional containing the source if found.
     */
    public Optional<V> byName(String name) {
        return entries().stream()
                .filter(source -> source.name().equalsIgnoreCase(name))
                .findFirst();
    }
}
