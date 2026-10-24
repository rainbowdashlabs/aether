/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.serialization.pojo.guild;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;

/**
 * POJO for {@link RichCustomEmoji} serialization.
 */
@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal", "unused"})
public class ReactionPOJO {
    @JsonProperty
    private String name;

    @JsonProperty
    private String id;

    @JsonProperty
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
