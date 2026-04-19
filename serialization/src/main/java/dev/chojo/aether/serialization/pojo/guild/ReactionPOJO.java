/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.serialization.pojo.guild;

import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;

/**
 * POJO for {@link RichCustomEmoji} serialization.
 */
@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal", "unused"})
public class ReactionPOJO {
    private String name;
    private String id;
    private String url;

    public ReactionPOJO(String name, String id, String url) {
        this.name = name;
        this.id = id;
        this.url = url;
    }

    public static ReactionPOJO generate(RichCustomEmoji emoji) {
        return new ReactionPOJO(emoji.getName(), emoji.getId(), emoji.getImageUrl());
    }
}
