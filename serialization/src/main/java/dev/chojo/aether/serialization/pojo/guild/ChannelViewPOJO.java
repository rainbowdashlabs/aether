/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.serialization.pojo.guild;

import dev.chojo.aether.serialization.pojo.guild.channel.CategoryPOJO;
import dev.chojo.aether.serialization.pojo.guild.channel.ChannelPOJO;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.SelfMember;
import net.dv8tion.jda.api.entities.channel.attribute.ICategorizableChannel;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * POJO for serializing all channels of a guild.
 */
@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal", "unused"})
public class ChannelViewPOJO {
    private List<ChannelPOJO> channels;
    private List<CategoryPOJO> categories;

    public ChannelViewPOJO(List<ChannelPOJO> channels, List<CategoryPOJO> categories) {
        this.channels = channels;
        this.categories = categories;
    }

    public static ChannelViewPOJO generate(Guild guild) {
        Map<Long, CategoryPOJO> categories = new LinkedHashMap<>();
        var channels = new LinkedList<ChannelPOJO>();
        SelfMember self = guild.getSelfMember();
        for (GuildChannel channel : guild.getChannels(true)) {
            if (channel instanceof Category category) {
                categories.put(category.getIdLong(), CategoryPOJO.generate(category));
            } else if (channel instanceof ICategorizableChannel categorizableChannel) {
                if (categorizableChannel.getParentCategory() != null) continue;
                channels.add(ChannelPOJO.from(channel));
            }
        }

        return new ChannelViewPOJO(channels, new LinkedList<>(categories.values()));
    }
}
