/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package dev.chojo.aether.serialization.pojo.guild.channel;

import net.dv8tion.jda.api.entities.channel.concrete.Category;

import java.util.LinkedList;
import java.util.List;

/**
 * POJO for {@link Category} serialization.
 */
@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal", "unused", "MismatchedQueryAndUpdateOfCollection"})
public class CategoryPOJO {
    private List<ChannelPOJO> channels;
    private String name;
    private String id;

    public CategoryPOJO(String name, String id) {
        this.name = name;
        this.id = id;
        channels = new LinkedList<>();
    }

    public static CategoryPOJO generate(Category category) {
        CategoryPOJO categoryPOJO = new CategoryPOJO(category.getName(), category.getId());
        category.getChannels().forEach(c -> categoryPOJO.addChannel(ChannelPOJO.from(c)));
        return categoryPOJO;
    }

    public void addChannel(ChannelPOJO channel) {
        channels.add(channel);
    }
}
