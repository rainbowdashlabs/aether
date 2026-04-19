/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.serialization.pojo;

import net.dv8tion.jda.api.interactions.DiscordLocale;

/**
 * POJO for {@link DiscordLocale} serialization.
 */
public record LocalePOJO(String code, String name, String nativeName, String internalName) {
    public static LocalePOJO of(DiscordLocale locale) {
        return new LocalePOJO(locale.getLocale(), locale.getLanguageName(), locale.getNativeName(), locale.name());
    }
}
