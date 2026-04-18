/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.supporter.configuration.modules.subscriptions.platform;

import dev.chojo.aether.supporter.registry.Key;

public record Platform(String name) implements Key {
    public static final Platform DISCORD = new Platform("DISCORD");
    public static final Platform KOFI = new Platform("KOFI");
    public static final Platform PATREON = new Platform("PATREON");
}
