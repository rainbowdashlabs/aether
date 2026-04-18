/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.supporter.registry;

/**
 * Represents a key that can be registered in a {@link Registry}.
 */
public interface Key {
    /**
     * The name of the key.
     * @return The name.
     */
    String name();
}
