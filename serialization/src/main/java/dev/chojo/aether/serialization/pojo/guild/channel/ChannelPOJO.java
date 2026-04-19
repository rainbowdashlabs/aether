/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.serialization.pojo.guild.channel;

import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;

/**
 * POJO for {@link GuildChannel} serialization.
 */
@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal", "unused"})
public class ChannelPOJO {
    private String name;
    private String id;
    private ChannelType type;
    private boolean visible;

    public ChannelPOJO(String name, String id, ChannelType type, boolean visible) {
        this.name = name;
        this.id = id;
        this.type = type;
        this.visible = visible;
    }

    public static ChannelPOJO from(GuildChannel channel) {
        return new ChannelPOJO(
                channel.getName(),
                channel.getId(),
                channel.getType(),
                channel.getGuild().getSelfMember().hasAccess(channel));
    }
}
