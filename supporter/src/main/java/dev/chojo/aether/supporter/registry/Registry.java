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

public class Registry<V extends Key> {
    private final Logger log = getLogger(Registry.class);
    private final Set<V> REGISTRY = new HashSet<>();
    private final Class<V> clazz;

    public Registry(Class<V> clazz) {
        this.clazz = clazz;
    }

    public Registry<V> register(V source) {
        if (REGISTRY.contains(source)) {
            throw new IllegalArgumentException("Source already registered");
        }
        log.info("Registered source {} in {}Registry", source.name(), clazz.getSimpleName());
        REGISTRY.add(source);
        return this;
    }

    public Set<V> sources() {
        return Collections.unmodifiableSet(REGISTRY);
    }

    public Optional<V> byName(String name) {
        return sources().stream()
                .filter(source -> source.name().equalsIgnoreCase(name))
                .findFirst();
    }
}
